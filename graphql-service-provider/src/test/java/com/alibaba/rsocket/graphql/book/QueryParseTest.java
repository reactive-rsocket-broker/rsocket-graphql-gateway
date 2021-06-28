package com.alibaba.rsocket.graphql.book;

import graphql.language.Document;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.language.Selection;
import graphql.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

public class QueryParseTest {

    @Test
    public void testParseQuery() {
        Parser parser = new Parser();
        // language=graphql
        Document document = parser.parseDocument("query {\n" +
                "  # routing=com.alibaba.book.Service\n" +
                "  bookById(id: \"book-1\") {\n" +
                "    id\n" +
                "    name\n" +
                "    pageCount\n" +
                "    author {\n" +
                "      firstName\n" +
                "      lastName\n" +
                "    }\n" +
                "  }\n" +
                "}");
        OperationDefinition operationDefinition = (OperationDefinition) document.getDefinitions().get(0);
        List<Selection> selections = operationDefinition.getSelectionSet().getSelections();
        for (Selection selection : selections) {
            Field field = (Field) selection;
            System.out.println(field.getName());
        }
    }
}
