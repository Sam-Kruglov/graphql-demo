package com.example.gqldemo.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "books")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String title;
    private String description;

    @Enumerated(STRING)
    private Genre genre;

    @ToString.Exclude
    @ManyToOne(fetch = LAZY)
    private Person author;

    @ToString.Exclude
    @ManyToMany(mappedBy = "reviewedBooksJpa")
    private Set<Person> reviewersJpa;

    public enum Genre {
        ACTION, FANTASY, CLASSIC
    }
}
