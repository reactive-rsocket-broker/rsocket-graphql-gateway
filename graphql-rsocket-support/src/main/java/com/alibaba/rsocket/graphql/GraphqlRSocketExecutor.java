package com.alibaba.rsocket.graphql;

import io.netty.buffer.ByteBuf;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * GraphQL RSocket Executor, execute method needed as entrance
 *
 * @author leijuan
 */
public interface GraphqlRSocketExecutor {

    default Mono<Map<String, Object>> execute(@Language("GraphQL") @NotNull String query) {
        return execute(query, null, null);
    }

    default Mono<Map<String, Object>> execute(@Language("GraphQL") String query,
                                              @NotNull Map<String, Object> variables) {
        return execute(query, variables, null);
    }

    /**
     * execute  GraphQL query, include Query, Mutation and Subscription
     *
     * @param query         query document
     * @param variables     variables
     * @param operationName operation name
     * @return map of the result that strictly follows the GraphQL spec
     */
    default Mono<Map<String, Object>> execute(@Language("GraphQL") @NotNull String query,
                                              @Nullable Map<String, Object> variables,
                                              @Nullable String operationName) {
        return execute(GraphqlJsonUtils.queryToJsonByteBuf(query, variables, operationName))
                .map(GraphqlJsonUtils::convertResultToMap);
    }

    /**
     * execute  GraphQL query, include Query, Mutation and Subscription
     *
     * @param invocationData invocation Map object: query, operationName, variables
     * @return json map of the result that strictly follows the GraphQL spec
     */
    Mono<ByteBuf> execute(ByteBuf invocationData);

}
