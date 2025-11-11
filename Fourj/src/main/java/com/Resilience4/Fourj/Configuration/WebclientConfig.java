package com.Resilience4.Fourj.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    @Bean
    public WebClient oneWebClient(WebClient.Builder builder){
        return builder.baseUrl("http://localhost:8081/serviceA").build();
    }

    @Bean
    public WebClient twoWebClient(WebClient.Builder builder){
        return builder.baseUrl("http://localhost:8082/serviceB").build();
    }


}