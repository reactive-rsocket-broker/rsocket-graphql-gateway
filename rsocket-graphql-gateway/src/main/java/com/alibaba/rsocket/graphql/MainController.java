package com.alibaba.rsocket.graphql;

import com.alibaba.rsocket.graphql.auth.JwtAuthenticationService;
import com.alibaba.rsocket.metadata.GSVRoutingMetadata;
import com.alibaba.rsocket.metadata.MessageMimeTypeMetadata;
import com.alibaba.rsocket.metadata.RSocketCompositeMetadata;
import com.alibaba.rsocket.metadata.RSocketMimeType;
import com.alibaba.rsocket.observability.RsocketErrorCode;
import com.alibaba.rsocket.upstream.UpstreamManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.RSocket;
import io.rsocket.util.ByteBufPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * main controller
 *
 * @author leijuan
 */
@Controller
public class MainController {
    @Autowired
    private JwtAuthenticationService authenticationService;
    @Value("${restapi.auth-required}")
    private boolean authRequired;
    @Autowired
    private ObjectMapper objectMapper;
    private static final MessageMimeTypeMetadata graphqlEncoding = new MessageMimeTypeMetadata(RSocketMimeType.Binary);
    private final RSocket rsocket;

    public MainController(UpstreamManager upstreamManager) {
        rsocket = upstreamManager.findBroker().getLoadBalancedRSocket();
    }

    @RequestMapping(value = "/{serviceName}/graphql", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ByteBuf>> graphqlGET(
            @PathVariable("serviceName") String serviceName,
            @RequestParam("query") String query,
            @RequestParam(value = "operationName", required = false) String operationName,
            @RequestParam(value = "variables", required = false) String variablesJson,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationValue
    ) {
        GraphQLInvocationData invocationData = new GraphQLInvocationData();
        invocationData.setQuery(query);
        invocationData.setOperationName(operationName);
        invocationData.setVariables(convertVariablesJson(variablesJson));
        return executeRequest(serviceName, invocationData, authorizationValue);
    }


    @PostMapping(value = "/{serviceName}/graphql", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ByteBuf>> graphqlPost(@PathVariable("serviceName") String serviceName,
                                                     @RequestParam(value = "query", required = false) String query,
                                                     @RequestParam(value = "operationName", required = false) String operationName,
                                                     @RequestParam(value = "variables", required = false) String variablesJson,
                                                     @RequestBody(required = false) String body,
                                                     @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationValue,
                                                     @RequestHeader(name = HttpHeaders.CONTENT_TYPE, required = false) String contentType) {

        GraphQLInvocationData invocationData;
        if (contentType.contains("application/json")) {
            try {
                invocationData = objectMapper.readValue(body, GraphQLInvocationData.class);
            } catch (Exception ignore) {
                invocationData = new GraphQLInvocationData();
            }
        } else {
            invocationData = new GraphQLInvocationData();
            if (contentType.contains("application/graphql")) {
                invocationData.setQuery(body);
            } else {
                invocationData.setQuery(query);
                invocationData.setOperationName(operationName);
                invocationData.setVariables(convertVariablesJson(variablesJson));
            }
        }
        return executeRequest(serviceName, invocationData, authorizationValue);
    }

    private Mono<ResponseEntity<ByteBuf>> executeRequest(String serviceName,
                                                         GraphQLInvocationData invocationData,
                                                         String authorizationValue) {
        boolean authenticated;
        if (!authRequired) {
            authenticated = true;
        } else {
            authenticated = authAuthorizationValue(authorizationValue);
        }
        if (!authenticated) {
            return Mono.error(new Exception(RsocketErrorCode.message("RST-500403")));
        }
        try {
            GSVRoutingMetadata routingMetadata = new GSVRoutingMetadata("", serviceName, "execute", "");
            RSocketCompositeMetadata compositeMetadata = RSocketCompositeMetadata.from(routingMetadata, graphqlEncoding);
            ByteBuf bodyBuf = Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(invocationData));
            return rsocket.requestResponse(ByteBufPayload.create(bodyBuf, compositeMetadata.getContent()))
                    .map(payload -> {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
                        return new ResponseEntity<>(payload.data(), headers, HttpStatus.OK);
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null) {
            return Collections.emptyMap();
        }
        try {
            //noinspection unchecked
            return objectMapper.readValue(jsonMap, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }


    private boolean authAuthorizationValue(String authorizationValue) {
        if (authorizationValue == null || authorizationValue.isEmpty()) {
            return false;
        }
        String jwtToken = authorizationValue;
        if (authorizationValue.contains(" ")) {
            jwtToken = authorizationValue.substring(authorizationValue.lastIndexOf(" ") + 1);
        }
        return authenticationService.auth(jwtToken) != null;
    }

}
