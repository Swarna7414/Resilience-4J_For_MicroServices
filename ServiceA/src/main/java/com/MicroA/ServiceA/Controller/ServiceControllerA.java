package com.MicroA.ServiceA.Controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/serviceA")
public class ServiceControllerA {

    @GetMapping("/get")
    public Map<String, Object> hitting(){
        return Map.of(
                "Message","PUka",
                "status", HttpStatus.OK,
                "malli","Hit Ayindi"
        );
    }

    @PostMapping("/post")
    public Map<String, Object> hitWithPost(@RequestParam String message){
        return Map.of(
                "message",message,
                "status",HttpStatus.OK.value()
        );
    }
}