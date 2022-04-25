create table books
(
    id          bigint not null,
    description varchar(255),
    genre       varchar(255),
    title       varchar(255),
    author_id   bigint,
    primary key (id)
);
create table people
(
    id         bigint not null,
    email      varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    primary key (id)
);
create table book_reviews
(
    reviewer_id bigint not null,
    book_id     bigint not null,
    primary key (reviewer_id, book_id)
);
alter table books
    add constraint FK_books__author_id foreign key (author_id) references people;
alter table book_reviews
    add constraint FK_book_reviews__books_id foreign key (book_id) references books;
alter table book_reviews
    add constraint FK_book_reviews__reviewer_id foreign key (reviewer_id) references people;
