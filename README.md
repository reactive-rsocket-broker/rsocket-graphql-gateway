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
* graphql-service-provider: GraphQL的RSocket服务提供者，使用Netflix DGS框架
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

# GraphQL Reactive support

### DGS

DGS 4.2.0版本后添加了Data Fetch的Mono/Flux返回类型支持，所以Reactive就内置啦，样例代码如下：

```
    @DgsQuery(field = "bookById")
    public Mono<Book> bookById(@InputArgument String id) {
        Book book = BOOKS.get(id);
        if (book == null) {
            return Mono.empty();
        } else {
            return Mono.just(book);
        }
    }
```

如果是列表类型，返回Flux就可以啦。

### GraphQL Java

* 异步执行graphQL.executeAsync()，将 CompletableFuture 转换为Mono

```
  public Mono<Object> execute(ExecutionInput executionInput) {
        CompletableFuture<ExecutionResult> future = graphQL.executeAsync(executionInput);
        return Mono.fromFuture(future).map(ExecutionResult::getData);
    }
```

*  DataFetcher with CompletableFuture<T> Generic type

```
public DataFetcher<CompletableFuture<Map<String, Object>>> bookById() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return Flux.fromIterable(BOOKS)
                    .filter(book -> book.get("id").equals(bookId))
                    .next()
                    .toFuture();
        };
    }
```

通过这两种方式，就可以非常方便地支持GraphQL查询的异步化。当然CompletableFuture和Reactive之间转换也就非常简单啦。

# References

* Alibaba RSocket Broker: https://github.com/alibaba/alibaba-rsocket-broker
* GraphQL Specification: https://spec.graphql.org/June2018/
* GraphQL Java: https://www.graphql-java.com/
* Netflix DGS: https://netflix.github.io/dgs/
* How Netflix Scales its API with GraphQL Federation: https://netflixtechblog.com/how-netflix-scales-its-api-with-graphql-federation-part-1-ae3557c187e2 https://netflixtechblog.com/how-netflix-scales-its-api-with-graphql-federation-part-2-bbe71aaec44a