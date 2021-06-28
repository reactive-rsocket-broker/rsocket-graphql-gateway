package com.alibaba.rsocket.graphql.book.graphqljava;

import com.alibaba.rsocket.graphql.book.SpringBootBaseTest;
import com.alibaba.rsocket.graphql.book.graphqljava.BookGraphqlServiceImpl;
import graphql.ExecutionInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BookGraphqlServiceImplTest extends SpringBootBaseTest {

    @Autowired
    private BookGraphqlServiceImpl graphqlService;

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute() {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query("query {\n" +
                "  bookById(id: \"book-1\") {\n" +
                "    id\n" +
                "    name\n" +
                "    pageCount\n" +
                "    author {\n" +
                "      firstName\n" +
                "      lastName\n" +
                "    }\n" +
                "  }\n" +
                "}").build();
        Map<String, Object> result = (Map<String, Object>) graphqlService.execute(executionInput).block();
        assertThat(result).containsKey("bookById");
        System.out.println(result);
    }
}
