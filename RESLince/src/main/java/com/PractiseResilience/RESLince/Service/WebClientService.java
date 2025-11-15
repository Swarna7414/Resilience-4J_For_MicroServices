package com.PractiseResilience.RESLince.Service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WebClientService {


    private static final String cBulkHead = "BulkHeadA";

    private static final Logger logger = LoggerFactory.getLogger(WebClientService.class);

    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private WebClient webClient;


    @Bulkhead(name = cBulkHead, type = Bulkhead.Type.THREADPOOL,fallbackMethod = "fallbackServiceAThreadPool")
    public CompletableFuture<Map<String, Object>> hitOne(String name, Integer age){
        counter.set(0);
        Map<String,Object> map = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/get").queryParam("name",name)
                .queryParam("age",age).build()).retrieve()
                .bodyToMono(Map.class).block();

        logger.info("This is the count {}",counter.getAndIncrement());


        return CompletableFuture.completedFuture(map);
    }


    public CompletableFuture<Map<String, Object>> fallbackServiceAThreadPool(String name, Integer age,Throwable exception){

        String reason = exception != null ? exception.getMessage() : "telidu ra pumka";

        logger.warn("In the Fall Back Method {}",reason);

        Map<String, Object> response = Map.of(
                "Message","IN the Fall back method",
                "reason",reason,
                "httpStatus",429
        );

        return CompletableFuture.completedFuture(response);
    }
}
