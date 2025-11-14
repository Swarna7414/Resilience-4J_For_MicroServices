package com.MicroA.ServiceA.Controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/serviceA")
public class ServiceControllerA {

    private Integer hit;

    private static final Logger logger = LoggerFactory.getLogger(ServiceControllerA.class);

    @GetMapping("/get")
    public Map<String, Object> getFirst(String name,Integer age){
        Integer hitM=0;
        hit++;
        hitM++;
        logger.info("THis is the Hit one {} with method hit {} , with name {}, age {}",hit++,hitM,name,age);
        return Map.of(
                "name",name,
                "age",age
        );
    }

    @GetMapping("/getparam")
    public String hitWithParam(@RequestParam String name){
        Integer hitM=0;
        hit++;
        hitM++;
        logger.info("THis is the Hitone {} with method hit {} with name {}",hit,hitM,name);
        return "Method Hit ayindi ra pumka "+name;
    }

    @PostMapping("/post")
    public ResponseEntity<Map<String, Object>> getPost(@RequestBody Map<String, Object> map){
        Integer hitM=0;
        hit++;
        hitM++;
        logger.info("This is the Hit one {} with method hit {} with map {}",hit,hitM,map);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}