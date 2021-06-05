package com.alibaba.rsocket.graphql.book;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"rsocket.disabled=true"})
public abstract class SpringBootBaseTest {
}
