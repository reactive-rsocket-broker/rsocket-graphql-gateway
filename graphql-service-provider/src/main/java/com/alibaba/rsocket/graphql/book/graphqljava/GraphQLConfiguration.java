package com.alibaba.rsocket.graphql.book.graphqljava;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Configuration
public class GraphQLConfiguration {
    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;
    @Value("classpath:schema/schema.graphqls")
    private Resource graphqlsResource;

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("bookById", graphQLDataFetchers.bookById()))
                .type(newTypeWiring("Book")
                        .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
                .build();
    }

    @Bean
    public GraphQL graphQL() throws Exception {
        String sdl = StreamUtils.copyToString(graphqlsResource.getInputStream(), StandardCharsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

}
