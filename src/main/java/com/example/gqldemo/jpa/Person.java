package com.example.gqldemo.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "people")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    @ToString.Exclude
    @OneToMany(mappedBy = "author")
    private Set<Book> authoredBooksJpa;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "book_reviews",
            joinColumns = @JoinColumn(name = "reviewer_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> reviewedBooksJpa;
}
