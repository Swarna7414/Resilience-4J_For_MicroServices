package com.PractiseResilience.RESLince.Controller;


import com.PractiseResilience.RESLince.Service.ResilienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/res")
public class ResController {

    @Autowired
    private ResilienService resilienService;


    @GetMapping("/one")
    public ResponseEntity<Map<String, Object>> hitOne(@RequestParam String name, @RequestParam Integer age){
        return ResponseEntity.ok(resilienService.callA(name, age));
    }

    @GetMapping("/two")
    public ResponseEntity<String> hitTwo(@RequestParam String name){
        return ResponseEntity.ok(resilienService.callB(name));
    }

    @PostMapping("/three")
    public ResponseEntity<Map<String, Object>> hitThree(@RequestBody Map<String, Object> map){
        return resilienService.callC(map);
    }
}
