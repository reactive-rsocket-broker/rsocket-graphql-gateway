package com.alibaba.rsocket.graphql.book.impl;

import graphql.schema.DataFetcher;
import org.eclipse.collections.api.factory.Maps;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class GraphQLDataFetchers {

    private static final List<Map<String, Object>> BOOKS = Arrays.asList(
            Maps.mutable.of("id", "book-1",
                    "name", "Harry Potter and the Philosopher's Stone",
                    "pageCount", "223",
                    "authorId", "author-1"),
            Maps.mutable.of("id", "book-2",
                    "name", "Moby Dick",
                    "pageCount", "635",
                    "authorId", "author-2"),
            Maps.mutable.of("id", "book-3",
                    "name", "Interview with the vampire",
                    "pageCount", "371",
                    "authorId", "author-3")
    );

    private static final List<Map<String, String>> AUTHORS = Arrays.asList(
            Maps.mutable.of("id", "author-1",
                    "firstName", "Joanne",
                    "lastName", "Rowling"),
            Maps.mutable.of("id", "author-2",
                    "firstName", "Herman",
                    "lastName", "Melville"),
            Maps.mutable.of("id", "author-3",
                    "firstName", "Anne",
                    "lastName", "Rice")
    );

    public DataFetcher<CompletableFuture<Map<String, Object>>> bookById() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return Flux.fromIterable(BOOKS)
                    .filter(book -> book.get("id").equals(bookId))
                    .next()
                    .toFuture();
        };
    }

    public DataFetcher<CompletableFuture<Map<String, String>>> getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");
            return Flux.fromIterable(AUTHORS)
                    .filter(author -> author.get("id").equals(authorId))
                    .next()
                    .toFuture();
        };
    }
}
