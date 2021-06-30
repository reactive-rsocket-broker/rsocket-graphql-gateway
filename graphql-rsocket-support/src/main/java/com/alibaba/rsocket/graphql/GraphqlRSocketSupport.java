package com.alibaba.rsocket.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * GraphQL RSocket support for GraphqlRSocketService
 *
 * @author leijuan
 */
public abstract class GraphqlRSocketSupport implements GraphqlRSocketExecutor {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<ByteBuf> execute(ByteBuf invocationData) {
        try {
            ExecutionInput executionInput = parseInput(invocationData.toString(StandardCharsets.UTF_8));
            return execute(executionInput).map(result -> Unpooled.wrappedBuffer(json(result.toSpecification())));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Flux<ByteBuf> subscribe(ByteBuf invocationData) {
        try {
            ExecutionInput executionInput = parseInput(invocationData.toString(StandardCharsets.UTF_8));
            return subscribe(executionInput).map(result -> Unpooled.wrappedBuffer(json(result.toSpecification())));
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    public abstract Flux<ExecutionResult> subscribe(ExecutionInput executionInput);


    public abstract Mono<ExecutionResult> execute(ExecutionInput executionInput);

    @SuppressWarnings("unchecked")
    private ExecutionInput parseInput(String text) throws Exception {
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
        return builder.build();
    }

    private byte[] json(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            return "{}".getBytes(StandardCharsets.UTF_8);
        }

    }
}
