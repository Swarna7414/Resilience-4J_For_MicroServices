package com.Resilience4.Fourj.Service;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.Map;
import java.util.Optional;


@Service
public class FourJService {

    private static final Logger logger = LoggerFactory.getLogger(FourJService.class);

    private final WebClient oneWebClient;

    private final WebClient twoWebClient;

    private final CircuitBreaker circuitBreaker;


    public FourJService(@Qualifier("oneWebClient") WebClient oneWebClient,
                        @Qualifier("twoWebClient") WebClient twoWebClient,
                        CircuitBreakerRegistry circuitBreakerRegistry) {
        this.oneWebClient = oneWebClient;
        this.twoWebClient = twoWebClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalServiceCB");
    }

//    @PostConstruct
//    void watchCb(CircuitBreakerRegistry reg) {
//        reg.circuitBreaker("externalServiceCB")
//                .getEventPublisher()
//                .onStateTransition(e -> logger.info("CB transition: {}", e.getStateTransition()));
//    }


    public Mono<Object> hitRequest(){

        return oneWebClient.get().uri("/get").retrieve().bodyToMono(Object.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(ex->{
                    logger.warn("hitRequest fallback due to: {}", ex.toString());
                    return Mono.just(Map.of("Fallback",true,"reason",ex.getMessage()));
                });
    }


    public Mono<Object> postHit(String message){
        return oneWebClient.post().uri(uriBuilder -> uriBuilder.path("/post")
                .queryParam("message",message).build()).retrieve().bodyToMono(Object.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(ex->{
                    logger.warn("hit Request FallBack due to :{}",ex.toString());
                    return Mono.just(Map.of("Fallback",true,"reaseon",ex.getMessage()));
                });
    }



    public Mono<String> shitRequest(String name){
        return twoWebClient.get().uri(uriBuilder -> uriBuilder.path("/get").queryParam("name",name).build()).retrieve().bodyToMono(String.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(ex->{
                    logger.warn("hit request Fallback due to :{}",ex.getMessage());
                    return Mono.just("Fallback"+name);
                });
    }


    public Mono<Object> sHit(String name, Optional<Integer> age){
        return twoWebClient.get().uri(uriBuilder -> uriBuilder.path("/post").queryParam("name",name)
                .queryParamIfPresent("age",age).build()).retrieve().bodyToMono(Object.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(ex->{
                    logger.warn("hit request Fallback due to :{}",ex.getMessage());
                    return Mono.just(Map.of("Fallback",true,"reason",ex.getMessage()));
                });

    }
}
