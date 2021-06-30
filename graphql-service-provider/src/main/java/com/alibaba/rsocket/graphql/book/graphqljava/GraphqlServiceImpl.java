package com.alibaba.rsocket.graphql.book.graphqljava;

import com.alibaba.rsocket.RSocketService;
import com.alibaba.rsocket.graphql.GraphqlRSocketExecutor;
import com.alibaba.rsocket.graphql.GraphqlRSocketSupport;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;


@RSocketService(serviceInterface = GraphqlRSocketExecutor.class, group = "book")
@Component
@Profile("graphqljava")
public class GraphqlServiceImpl extends GraphqlRSocketSupport implements GraphqlRSocketExecutor {
    @Autowired
    private GraphQL graphQL;

    @Override
    public Mono<ExecutionResult> execute(ExecutionInput executionInput) {
        CompletableFuture<ExecutionResult> future = graphQL.executeAsync(executionInput);
        return Mono.fromFuture(future);
    }
}
