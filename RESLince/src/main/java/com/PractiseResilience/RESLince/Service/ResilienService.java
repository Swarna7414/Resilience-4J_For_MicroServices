package com.PractiseResilience.RESLince.Service;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ResilienService {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ResilienService.class);

    private static final String CBreakerName="BreakerA";


    private final AtomicInteger hitCounter = new AtomicInteger(0);

    private static final String baseUri = "http://localhost:8081/serviceA";


//    @CircuitBreaker(name = CBreakerName,fallbackMethod = "FallBackForcallA")
//    @Retry(name = CBreakerName)
    @RateLimiter(name=CBreakerName)
    public Map<String,Object> callA(String name, Integer age){


        String uri = UriComponentsBuilder.fromHttpUrl(baseUri).path("/get").queryParam("name",name)
                .queryParam("age",age).toUriString();

        Map<String, Object> map = restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();

        int mHit=hitCounter.incrementAndGet();
        logger.info("This is Retry {} time {}",mHit, LocalTime.now());
        return map;
    }


    @CircuitBreaker(name=CBreakerName,fallbackMethod = "fallBackForcallB")
    public String callB(String name){
        String uri = UriComponentsBuilder.fromHttpUrl(baseUri).path("/getparam").queryParam("name",name)
                .toUriString();

        return restTemplate.getForObject(uri,String.class);
    }


    @CircuitBreaker(name = CBreakerName, fallbackMethod = "fallBackforcallC")
    public ResponseEntity<Map<String, Object>> callC(Map<String, Object> map){
        String uri = UriComponentsBuilder.fromHttpUrl(baseUri).path("/post").toUriString();

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate
                .exchange(uri,HttpMethod.POST,httpEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

        return responseEntity;
    }

    private Map<String,Object> FallBackForcallA(String name, Integer age,Exception exception){
        return Map.of(
                "exception",exception.getMessage(),
                "message","This is a Fall Back Method",
                "name",name,
                "age",age

        );
    }

    private String fallBackForcallB(String name,Exception exception){
        return exception.getMessage()+"for the name of "+name;
    }

    private ResponseEntity<Map<String, Object>> fallBackforcallC(Map<String, Object> map,Exception exception){
        Map<String, Object> realmap = new HashMap<>();
        realmap.putAll(map);
        realmap.put("message",exception.getMessage());
        return ResponseEntity.badRequest().body(realmap);
    }

}