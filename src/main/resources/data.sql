insert into people(id, email, first_name, last_name)
values (1, 'joe@gmail.com', 'Joe', 'Black'),
       (2, 'mike@gmail.com', 'Mike', 'Smith'),
       (3, null, 'Peter', 'Goodman'),
       (4, 'george@gmai.com', 'George', 'Fischer');
insert into books(id, description, title, genre, author_id)
values (1, 'What is it, pros, cons, when to use it, when to not use it, maybe implementation details if got time.',
        'Introduction to GraphQL', 'ACTION', 1),
       (2, null, 'Book 2', 'FANTASY', 1),
       (3, 'about book 3', 'Book 3', 'CLASSIC', 1),
       (4, 'about book 4', 'Book 4', 'ACTION', 2),
       (5, null, 'Book 5', 'CLASSIC', 3),
       (6, 'about book 6', 'Book 6', 'FANTASY', 4);
insert into book_reviews (book_id, reviewer_id)
values (1, 2),
       (1, 4),
       (2, 2),
       (3, 2),
       (4, 1),
       (4, 4),
       (5, 1),
       (5, 2),
       (5, 4),
       (6, 1),
       (6, 2);