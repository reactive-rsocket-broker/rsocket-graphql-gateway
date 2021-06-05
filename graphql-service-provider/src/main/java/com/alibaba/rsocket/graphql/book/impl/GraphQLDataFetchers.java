package com.alibaba.rsocket.graphql.book.impl;

import graphql.schema.DataFetcher;
import org.eclipse.collections.api.factory.Maps;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public DataFetcher<Map<String, Object>> bookById() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return BOOKS
                    .stream()
                    .filter(book -> book.get("id").equals(bookId))
                    .findFirst()
                    .orElse(BOOKS.get(0));
        };
    }

    public DataFetcher<Map<String, String>> getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");
            return AUTHORS
                    .stream()
                    .filter(author -> author.get("id").equals(authorId))
                    .findFirst()
                    .orElse(null);
        };
    }
}
