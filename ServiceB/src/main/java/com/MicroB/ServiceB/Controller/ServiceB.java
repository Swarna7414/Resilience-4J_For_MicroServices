package com.MicroB.ServiceB.Controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/serviceB")
public class ServiceB {

    @GetMapping("/get")
    public String hit(@RequestParam String name){
        return "Hi ra Puka"+name;
    }

    @PostMapping("/post")
    public Map<String, Object> hitWithParam(@RequestParam String name, @RequestParam Integer age){
        return Map.of(
                "name",name,
                "age",age,
                "Hitting",true
        );
    }

}