package com.example.demo;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.junit.jupiter.api.Test;

@SpringBootTest
@EnableTransactionManagement
class CustSm1ApplicationTests {

    private final ApplicationContext applicationContext;

    CustSm1ApplicationTests(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }
}