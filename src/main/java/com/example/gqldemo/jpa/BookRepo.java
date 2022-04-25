package com.example.gqldemo.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepo extends CrudRepository<Book, Long> {

    @Query(value = "select book_id from book_reviews where reviewer_id = :reviewerId", nativeQuery = true)
    List<Long> findAllIdsByReviewerId(Long reviewerId);

    List<Book> findAllByAuthorId(Long authorId);
}
