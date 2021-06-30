package com.alibaba.rsocket.graphql.book.dgs;

import com.alibaba.rsocket.RSocketService;
import com.alibaba.rsocket.graphql.GraphqlRSocketExecutor;
import com.alibaba.rsocket.graphql.GraphqlRSocketSupport;
import com.netflix.graphql.dgs.reactive.DgsReactiveQueryExecutor;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;


@RSocketService(serviceInterface = GraphqlRSocketExecutor.class, group = "book")
@Component
public class GraphqlServiceDGSImpl extends GraphqlRSocketSupport implements GraphqlRSocketExecutor {
    @Autowired
    private DgsReactiveQueryExecutor reactiveQueryExecutor;

    @Override
    public Mono<ExecutionResult> execute(ExecutionInput executionInput) {
        return reactiveQueryExecutor.execute(executionInput.getQuery(),
                executionInput.getVariables(),
                executionInput.getOperationName());
    }

    @Override
    public Flux<ExecutionResult> subscribe(ExecutionInput executionInput) {
        Map<String, Object> variables = executionInput.getVariables();
        if (variables == null) {
            variables = Collections.emptyMap();
        }
        return reactiveQueryExecutor.execute(executionInput.getQuery(), variables)
                .flatMapMany(executionResult -> Flux.from(executionResult.<Publisher<ExecutionResult>>getData()));
    }
}
