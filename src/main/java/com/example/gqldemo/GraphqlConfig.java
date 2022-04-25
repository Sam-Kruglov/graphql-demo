package com.example.gqldemo;

import com.example.gqldemo.jpa.Book;
import com.example.gqldemo.jpa.BookRepo;
import com.example.gqldemo.jpa.Person;
import com.example.gqldemo.jpa.PersonRepo;
import graphql.GraphQLError;
import graphql.GraphqlErrorException;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Configuration
@Slf4j
public class GraphqlConfig {

    @Autowired
    public void bookLoaders(BatchLoaderRegistry registry, BookRepo repo) {
        registry.forTypePair(Long.class, Book.class).registerMappedBatchLoader((ids, env) ->
                Mono.fromSupplier(() -> repo.findAllById(ids))
                        .flatMapMany(Flux::fromIterable)
                        .collectMap(Book::getId)
                        .subscribeOn(Schedulers.boundedElastic())
        );
    }

    @Autowired
    public void personLoaders(BatchLoaderRegistry registry, PersonRepo repo) {
        registry.forTypePair(Long.class, Person.class).registerMappedBatchLoader((ids, env) ->
                Mono.fromSupplier(() -> repo.findAllById(ids))
                        .flatMapMany(Flux::fromIterable)
                        .collectMap(Person::getId)
                        .subscribeOn(Schedulers.boundedElastic())
        );
    }

    @Bean
    public DataFetcherExceptionResolver exceptionResolver() {
        return new DataFetcherExceptionResolverAdapter() {
            @Override
            protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
                log.debug("error: ", ex);
                return GraphqlErrorException.newErrorException()
                        .message(ex.getMessage())
                        .cause(ex)
                        .extensions(Map.of("myCustomField", "customValue"))
                        .sourceLocation(env.getExecutionStepInfo().getField().getSingleField().getSourceLocation())
                        .path(env.getExecutionStepInfo().getPath().toList())
                        .build();
            }
        };
    }
}
