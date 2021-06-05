RSocket GraphQL Gateway
=======================

RSocket GraphQL Gateway is an HTTP entrance to access GraphQL Services that register on RSocket Broker.

![GraphQL RSocket Gateway](grapql-rsocke-gateway.png)

# Features

* Load balance support for GraphQL Service
* Access GraphQL Service by RSocket
* Query merge support

# Modules

* rsocket-graphql-gateway: 基于RSocket的GraphQL HTTP Gateway
* graphql-service-provider: GraphQL的RSocket服务提供者
* graphql-rsocket-support: 支持GraphQL服务快速接入到RSocket Broker

# How to start?

* Start the RSocket Broker: `docker-compose up -d`
* Start the graphql-rsocket-provider
* Start the rsocket-graphql-gateway
* Please visit index.http and test

# 工作原理

GraphQL和RSocket Broker对接，主要介入了service namespace的概念，也就是不同的GraphQL Schema使用不同的服务分组

* namespace: 如 `com.alibaba.rsocket.graphql.book.BookGraphqlService` 在Gateway上对应的HTTP地址为 `http://localhost:8383/com.alibaba.rsocket.graphql.book.BookGraphqlService/graphql`
* routing: 不同的namespace会路由到对应的服务提供者

# References

* Alibaba RSocket Broker: https://github.com/alibaba/alibaba-rsocket-broker