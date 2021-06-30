package com.alibaba.rsocket.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphqlJsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> convertResultToMap(ByteBuf byteBuf) {
        try {
            //noinspection unchecked
            return objectMapper.readValue(byteBuf.toString(StandardCharsets.UTF_8), Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public static ByteBuf queryToJsonByteBuf(@NotNull String query,
                                             @Nullable Map<String, Object> variables,
                                             @Nullable String operationName) {
        try {
            Map<String, Object> invocation = new HashMap<>();
            invocation.put("query", query);
            if (variables != null) {
                invocation.put("variables", variables);
            }
            if (operationName != null) {
                invocation.put("operationName", operationName);
            }
            return Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(invocation));
        } catch (Exception e) {
            return Unpooled.wrappedBuffer("{}".getBytes(StandardCharsets.UTF_8));
        }
    }


}
