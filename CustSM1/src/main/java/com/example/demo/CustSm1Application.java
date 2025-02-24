package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableTransactionManagement
public class CustSm1Application {

	public static void main(String[] args) {
		SpringApplication.run(CustSm1Application.class, args);
	}

	 @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
}
