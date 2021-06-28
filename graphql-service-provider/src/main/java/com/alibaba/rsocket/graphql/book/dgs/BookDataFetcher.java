package com.alibaba.rsocket.graphql.book.dgs;

import com.netflix.graphql.dgs.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@DgsComponent
public class BookDataFetcher {
    private static final Map<String, Book> BOOKS = Stream.of(
            new Book("book-1", "Harry Potter and the Philosopher's Stone", 223, "author-1"),
            new Book("book-2", "Moby Dick", 635, "author-2"),
            new Book("book-3", "Interview with the vampire", 371, "author-3")
    ).collect(Collectors.toMap(Book::getId, book -> book));

    private static final Map<String, Author> AUTHORS = Stream.of(
            new Author("author-1", "Joanne", "Rowling"),
            new Author("author-2", "Herman", "Melville"),
            new Author("author-3", "Anne", "Rice")
    ).collect(Collectors.toMap(Author::getId, author -> author));

    @DgsQuery(field = "bookById")
    public Mono<Book> bookById(@InputArgument String id) {
        Book book = BOOKS.get(id);
        if (book == null) {
            return Mono.empty();
        } else {
            return Mono.just(book);
        }
    }

    @DgsData(parentType = "Book", field = "author")
    public Author author(DgsDataFetchingEnvironment dfe) {
        Book book = dfe.getSource();
        return AUTHORS.get(book.getAuthorId());
    }

}
