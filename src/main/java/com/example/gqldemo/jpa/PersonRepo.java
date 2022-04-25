package com.example.gqldemo.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepo extends CrudRepository<Person, Long> {

    @Query(value = "select reviewer_id from book_reviews where book_id = :bookId", nativeQuery = true)
    List<Long> findAllReviewerIdsByBookId(Long bookId);
}
