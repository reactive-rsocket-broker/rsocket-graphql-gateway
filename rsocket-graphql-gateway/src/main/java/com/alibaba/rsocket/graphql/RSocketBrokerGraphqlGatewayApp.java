package com.alibaba.rsocket.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RSocket Broker GraphQL Gateway App
 *
 * @author leijuan
 */
@SpringBootApplication
public class RSocketBrokerGraphqlGatewayApp {
    public static void main(String[] args) {
        SpringApplication.run(RSocketBrokerGraphqlGatewayApp.class, args);
    }

}
