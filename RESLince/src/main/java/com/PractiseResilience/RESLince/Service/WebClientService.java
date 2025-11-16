package com.PractiseResilience.RESLince.Service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WebClientService {


    private static final String cBulkHead = "BulkHeadA";

    private static final String CrateName = "BreakerA";

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


    @RateLimiter(name = CrateName, fallbackMethod = "RateFallBack")
    public Map<String, Object> RateLimit(String name, Integer age){

        counter.set(0);

        logger.info("This is the count {}",counter.getAndIncrement());

        return webClient.get().uri(uriBuilder -> uriBuilder.path("/get").queryParam("name",name).queryParam("age",age).build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}).block();

    }


    public Map<String, Object> RateFallBack(String name, Integer age, Throwable throwable){

        String reason = throwable !=null ? throwable.getClass().getSimpleName() : "Unkown Error";

        logger.info("This is a Fallback Method with name {} and age {} ,reason {} ",name,age,reason);

        return Map.of(
                "message","TO many Requests for the Service A please Try again Later",
                "httpStatus",429,
                "name",name,
                "age",age,
                "reason",reason
        );

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
