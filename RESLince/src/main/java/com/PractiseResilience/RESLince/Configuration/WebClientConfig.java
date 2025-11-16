package com.PractiseResilience.RESLince.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webclinet(){
        return WebClient.builder().baseUrl("http://localhost:8081/serviceA").build();
    }
}
