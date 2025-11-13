package com.Resilience4.Fourj.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceClient {

    @Value("${ServiceA.base-url}")
    private String baseUrlA;

    @Value("${ServiceB.base-url}")
    private String baseUrlB;

    private int hit=0;

    @Autowired
    private final RestTemplate restTemplate;

    private static Logger logger = LoggerFactory.getLogger(UserServiceClient.class);


    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @CircuitBreaker(name="externalServiceCB",fallbackMethod ="fallbackServiceAGet")
    @Retry(name = "externalServiceRetry")
    @RateLimiter(name="externalServiceRL", fallbackMethod = "fallbackServiceAGetRL")
    public Map<String, Object> hitServiceA() throws InterruptedException {

        hit++;
        logger.info("Application has been Hitted {} here and this the hit {}", LocalDateTime.now(),hit);

        String uri = UriComponentsBuilder.fromHttpUrl(baseUrlA).path("get").toUriString();

        Thread thread = new Thread();
        thread.sleep(5_000L);

        Map<String, Object> map = restTemplate.getForObject(uri, Map.class);

        return map;

    }


    @CircuitBreaker(name = "externalServiceCB", fallbackMethod = "fallbackGetB")
    @Retry(name = "externalServiceRetry")
    @TimeLimiter(name="externalServiceTL", fallbackMethod = "fallbackServiceAGetAsync")
    public CompletableFuture<Map<String, Object>> hitServiceAWithPost(String message){
        //time limiter only works with the Mono, Flux or CompletableFuture<T> with async

        logger.info("application has been hittied {} here", LocalDateTime.now());

        return CompletableFuture.supplyAsync(()->{

            String uri = UriComponentsBuilder.fromHttpUrl(baseUrlA).path("post").queryParam("message",message).toUriString();

            Map<String, Object> map = restTemplate.exchange(uri, HttpMethod.POST, HttpEntity.EMPTY, Map.class).getBody();

            return map;

        });
    }


    @CircuitBreaker(name = "externalServiceCB",fallbackMethod = "fallbackServiceAPost")
    @Retry(name = "externalServiceRetry")
    public String hitServiceB(String name){

        logger.info("Service B is hited with Get");

        logger.info("Application has been Hitted {} here", LocalDateTime.now());

        String uri=UriComponentsBuilder.fromHttpUrl(baseUrlB).path("get").queryParam("name",name).toUriString();

        return restTemplate.getForObject(uri,String.class);

    }


    @CircuitBreaker(name = "externalServiceCB",fallbackMethod = "FallbackPostB")
    @Retry(name = "externalServiceRetry")
    public Map hitWithParam(String name, Integer age){

        logger.info("Service B Post Get's Hitted");

        logger.info("Application has been Hitted {} here", LocalDateTime.now());

        String uri=UriComponentsBuilder.fromHttpUrl(baseUrlB).path("post").queryParam("name",name).queryParam("age",age).toUriString();

        Map map = restTemplate.exchange(uri,HttpMethod.POST,HttpEntity.EMPTY, Map.class).getBody();

        return map;
    }


    public Map<String, Object> fallbackServiceAGet(Throwable t){
        logger.info("Application has been Hitted {} here", LocalDateTime.now());
        return Map.of(
                "Message","Circuit breaker fallback (ServiceA Get)",
                "httpstatus",HttpStatus.SERVICE_UNAVAILABLE.value(),
                "reason", t != null ? t.toString() : "unknown"
        );
    }

    public Map<String, Object> fallbackServiceAPost(String message,Throwable t){
        return Map.of(
                "Message","Circut Breaker for the Fallback (Serivce B Post)",
                "messafge",message,
                "httpstatus",HttpStatus.SERVICE_UNAVAILABLE.value(),
                "reason", t != null ? t.toString() : "unkonwn"
        );
    }

    public String fallbackGetB(String name, Throwable throwable){
        return "Circuit breaker fallback (ServiceB GET) for name"+name;
    }


    public Map<String, Object> FallbackPostB(String name, Integer age, Throwable t){
        return Map.of(
                "meaase","Puka Poindi ra",
                "httpStatus",HttpStatus.SERVICE_UNAVAILABLE.value(),
                "values",name,
                "age",age,
                "reason", t != null ? t.toString() : "unkonwn"
        );
    }

    public CompletableFuture<Map<String, Object>> fallbackServiceAGetAsync(String message,Throwable throwable){
        logger.warn("FallBack Service has been activated at {} reason of {}"
                ,LocalDateTime.now(),
                throwable != null ? throwable.toString() : "unkonwn ra pumka");
        return CompletableFuture.completedFuture(Map.of(
                "message","Time Limiter Activated ra Pumka",
                "httpStatus", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "messagereal",message,
                "reason", throwable != null ? throwable.toString() : "Unknown ra Pumka"
        ));
    }

    public Map<String, Object> fallbackServiceAGetRL(Throwable throwable){

        String reason = throwable !=null ? throwable.getClass().getSimpleName() + ":" + throwable.getMessage() : "unknown";
        return Map.of(
                "Message","Rate Limited ra Pumlka",
                "httpstatus",429,
                reason,reason
        );
    }

}