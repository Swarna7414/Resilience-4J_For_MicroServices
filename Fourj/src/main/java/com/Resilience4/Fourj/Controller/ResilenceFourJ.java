package com.Resilience4.Fourj.Controller;


import com.Resilience4.Fourj.Service.FourJService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/resilience")
public class ResilenceFourJ {

    @Autowired
    public FourJService service;

    @GetMapping("/hitone")
    public Mono<Object> hitOne(){
        return service.hitRequest();
    }

    @PostMapping("/hit/two")
    public Mono<Object> hitTwo(@RequestParam String message){
        return service.postHit(message);
    }

    @GetMapping("/hit/second")
    public Mono<String> hitSecond(@RequestParam String name){
        return service.shitRequest(name);
    }

    @PostMapping
    public Mono<Object> hitSecondWithPost(String name, Optional<Integer> age){
        return service.sHit(name,age);
    }


}
