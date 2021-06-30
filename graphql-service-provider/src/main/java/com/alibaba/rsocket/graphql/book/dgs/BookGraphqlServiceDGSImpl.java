package com.alibaba.rsocket.graphql.book.dgs;

import com.alibaba.rsocket.RSocketService;
import com.alibaba.rsocket.graphql.GraphqlRSocketSupport;
import com.alibaba.rsocket.graphql.book.BookGraphqlService;
import com.netflix.graphql.dgs.reactive.DgsReactiveQueryExecutor;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@RSocketService(serviceInterface = BookGraphqlService.class)
@Component
public class BookGraphqlServiceDGSImpl extends GraphqlRSocketSupport implements BookGraphqlService {
    @Autowired
    private DgsReactiveQueryExecutor reactiveQueryExecutor;

    @Override
    public Mono<ExecutionResult> execute(ExecutionInput executionInput) {
        return reactiveQueryExecutor.execute(executionInput.getQuery(),
                executionInput.getVariables(),
                executionInput.getOperationName());
    }
}
