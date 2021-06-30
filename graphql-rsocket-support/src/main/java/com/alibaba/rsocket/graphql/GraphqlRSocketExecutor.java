package com.alibaba.rsocket.graphql;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Mono;

/**
 * GraphQL RSocket Executor, execute method needed as entrance
 *
 * @author leijuan
 */
public interface GraphqlRSocketExecutor {

    /**
     * execute  GraphQL query, include Query, Mutation and Subscription
     *
     * @param invocationData invocation Map object: query, operationName, variables
     * @return json map of the result that strictly follows the GraphQL spec
     */
    Mono<ByteBuf> execute(ByteBuf invocationData);

}
