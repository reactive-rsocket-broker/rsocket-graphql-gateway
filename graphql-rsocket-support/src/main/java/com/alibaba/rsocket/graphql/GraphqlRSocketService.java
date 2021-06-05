package com.alibaba.rsocket.graphql;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Mono;

/**
 * GraphQL RSocket Service, execute method needed as entrance
 *
 * @author leijuan
 */
public interface GraphqlRSocketService {

    /**
     * GraphQL execute entrance
     *
     * @param invocationData invocation Map object: query, operationName, variables
     * @return json with "data" attribute
     */
    Mono<ByteBuf> execute(ByteBuf invocationData);

}
