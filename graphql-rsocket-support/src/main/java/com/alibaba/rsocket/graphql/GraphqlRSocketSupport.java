package com.alibaba.rsocket.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * GraphQL RSocket support for GraphqlRSocketService
 *
 * @author leijuan
 */
public abstract class GraphqlRSocketSupport implements GraphqlRSocketService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public Mono<ByteBuf> execute(ByteBuf byteBuf) {
        String text = byteBuf.toString(StandardCharsets.UTF_8);
        try {
            Map<String, Object> map = objectMapper.readValue(text, Map.class);
            ExecutionInput.Builder builder = ExecutionInput.newExecutionInput();
            String query = (String) map.get("query");
            String operationName = (String) map.get("operationName");
            Map<String, Object> variables = (Map<String, Object>) map.get("variables");
            if (query != null) {
                builder.query(query);
            } else {
                builder.query("");
            }
            if (operationName != null) {
                builder.operationName(operationName);
            }
            if (variables != null) {
                builder.variables(variables);
            }
            ExecutionInput executionInput = builder.build();
            return execute(executionInput).map(result -> Unpooled.wrappedBuffer(json(result)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public abstract Mono<Object> execute(ExecutionInput executionInput);


    private byte[] json(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            return "{}".getBytes(StandardCharsets.UTF_8);
        }

    }
}
