package com.MicroA.ServiceA.Controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/serviceA")
public class ServiceControllerA {

    int hit =0;

    int hit2=0;

    private static final Logger logger = LoggerFactory.getLogger(ServiceControllerA.class);

    @GetMapping("/get")
    public Map<String, Object> hitting(){
        hit++;
        logger.info("hit number {} occured",hit);
        return Map.of(
                "Message","PUka",
                "status", HttpStatus.OK,
                "malli","Hit Ayindi"
        );
    }

    @PostMapping("/post")
    public Map<String, Object> hitWithPost(@RequestParam String message) throws InterruptedException {

//        Thread thread = new Thread();
//        thread.sleep(50_000L);

        hit2++;
        logger.info("Hit number {} occured",hit2);
        return Map.of(
                "message",message,
                "status",HttpStatus.OK.value()
        );
    }
}