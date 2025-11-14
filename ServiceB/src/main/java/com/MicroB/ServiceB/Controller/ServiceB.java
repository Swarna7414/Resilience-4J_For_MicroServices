package com.MicroB.ServiceB.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/serviceB")
public class ServiceB {

    private static final Logger logger = LoggerFactory.getLogger(ServiceB.class);

    private static Integer hit = 0;

    @GetMapping("/get")
    public String hitRequest(@RequestParam String name){
        Integer hitM=0;
        hit++;
        hitM++;
        logger.info("this is the Hit {} method hit {}",hit,hitM);
        return "Hi ra Puka"+name;
    }

    @PostMapping("/post")
    public Map<String, Object> hitWithParam(@RequestParam String name, @RequestParam Integer age){
        Integer hitM=0;
        hit++;
        hitM++;
        logger.info("this is the Hit {} method hit {}",hit,hitM);
        return Map.of(
                "name",name,
                "age",age,
                "Hitting",true
        );
    }

}