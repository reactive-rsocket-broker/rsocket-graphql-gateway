package com.alibaba.rsocket.graphql;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * GraphQL RSocket Client
 *
 * @author leijuan
 */
public interface GraphqlRSocketClient {

    Mono<ByteBuf> query(String namespace, String query, Map<String, Object> variables);

    Mono<ByteBuf> mutation(String namespace, String mutation, Map<String, Object> variables);

    Flux<ByteBuf> subscription(String namespace, String subscription, Map<String, Object> variables);
}
