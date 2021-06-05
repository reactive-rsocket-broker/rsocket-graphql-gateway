GraphQL with RSocket
====================

# module

* query: request/response
* mutation: request/response
* subscription: request/stream

# GraphQL服务分组

namespace结构


# 多分组聚合

GraphQL Query需要添加完整的namespace，这样方便进行路由，如下：

```graphql
type Query {
  com_alibaba_rsocket_graphql_book_OrderGraphqlService_TransactionById(id: ID): PlaceOrder
  com_alibaba_rsocket_graphql_book_ItemGraphqlService_itemById(id: ID): Item
}
```

这里要对Query进行分拆，然后分到不同的服务组上。
