package com.example.gqldemo;

import com.example.gqldemo.jpa.Book;
import com.example.gqldemo.jpa.BookRepo;
import com.example.gqldemo.jpa.Person;
import com.example.gqldemo.jpa.PersonRepo;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.groupingBy;

@Controller
@RequiredArgsConstructor
public class MyController {

    private static final String FAIL_ON_REVIEWERS_KEY = "FAIL_ON_REVIEWERS_KEY";

    private final BookRepo bookRepo;
    private final PersonRepo personRepo;

    private final Sinks.Many<Book> bookChangedSink = Sinks.many().multicast().directBestEffort();

    @QueryMapping
    public Iterable<Book> books(@Argument Boolean failOnReviewers, GraphQLContext context) {
        context.put(FAIL_ON_REVIEWERS_KEY, failOnReviewers);
        return bookRepo.findAll();
    }

    @QueryMapping
    public Optional<Book> book(@Argument Long id, @Argument Boolean failOnReviewers, GraphQLContext context) {
        context.put(FAIL_ON_REVIEWERS_KEY, failOnReviewers);
        return bookRepo.findById(id);
    }

    @QueryMapping
    public Iterable<Person> people() {
        return personRepo.findAll();
    }

    @QueryMapping
    public Optional<Person> person(@Argument Long id) {
        return personRepo.findById(id);
    }

    @MutationMapping
    public Book editBook(@Argument Long id, @Argument String description) {
        val book = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("book not found"));
        book.setDescription(description);
        bookRepo.save(book);
        val res = bookChangedSink.tryEmitNext(book);
        return book;
    }

    @SubscriptionMapping
    public Flux<Book> watchBook(@Argument Long id) {
        return bookChangedSink.asFlux()
                .doOnNext(System.out::println)
                .filter(book -> book.getId().equals(id));
    }

    @Value
    public static class BookChangedEvent {
        Long id;
    }

    //region relationships
    @BatchMapping
    public Mono<Map<Book, Person>> author(List<Book> books) {
        return Mono.fromSupplier(() -> {
            val booksByAuthorId = books.stream().collect(groupingBy(book -> book.getAuthor().getId()));
            Map<Book, Person> result = new HashMap<>();
            personRepo.findAllById(booksByAuthorId.keySet()).forEach(person -> booksByAuthorId.get(person.getId()).forEach(book -> result.put(book, person)));
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @SchemaMapping
    public CompletableFuture<List<Person>> reviewers(Book book, DataLoader<Long, Person> loader, GraphQLContext context) {
        if (context.get(FAIL_ON_REVIEWERS_KEY)) return CompletableFuture.failedFuture(new RuntimeException("oops"));
        //todo check up on https://github.com/graphql-java/java-dataloader/discussions/93
        return loader.loadMany(personRepo.findAllReviewerIdsByBookId(book.getId()));
    }

    @SchemaMapping
    public List<Book> authoredBooks(Person author) {
        //todo check up on https://github.com/graphql-java/java-dataloader/discussions/93
        return bookRepo.findAllByAuthorId(author.getId());
    }

    @SchemaMapping
    public CompletableFuture<List<Book>> reviewedBooks(Person reviewer, DataLoader<Long, Book> loader) {
        //todo check up on https://github.com/graphql-java/java-dataloader/discussions/93
        return loader.loadMany(bookRepo.findAllIdsByReviewerId(reviewer.getId()));
    }
    //endregion
}
