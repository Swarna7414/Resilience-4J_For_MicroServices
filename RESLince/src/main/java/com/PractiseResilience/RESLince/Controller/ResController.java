package com.PractiseResilience.RESLince.Controller;

import com.PractiseResilience.RESLince.Service.ResilienService;
import com.PractiseResilience.RESLince.Service.WebClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/res")
public class ResController {

    @Autowired
    private ResilienService resilienService;

    @Autowired
    private WebClientService webClientService;


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


    @GetMapping("/web/hit")
    public ResponseEntity<Map<String, Object>> hitWebClient(@RequestParam String name, @RequestParam Integer age){
        Map<String, Object> map = webClientService.hitOne(name,age).join();

        return ResponseEntity.ok(map);

    }

    @GetMapping("/limitrate")
    public ResponseEntity<Map<String, Object>> hitRateLimit(@RequestParam String name, @RequestParam Integer age){

        Map<String, Object> map = webClientService.RateLimit(name,age);

        return ResponseEntity.ok(map);

    }

    @GetMapping("/testrate")
    public String testRateLimit() throws InterruptedException{
        List<Map<String, Object>> futures = new ArrayList<>();

        for (int i=0; i<=50; i++){
            int n=1;
            n++;
            Map<String, Object> f = webClientService.RateLimit("user"+n,20+n);

            futures.add(f);
        }

        futures.forEach(n->{
            System.out.println(n);
        });

        return "DOne ra pumka";
    }


    @GetMapping("/testbulkhead")
    public String getA() throws InterruptedException{
        List<CompletableFuture<Map<String,Object>>> futures = new ArrayList<>();

        for(int i=0; i<=50;i++){
            int n=1;
            CompletableFuture<Map<String, Object>> f = webClientService.hitOne("user"+n,20+n);
            futures.add(f);
        }

        futures.forEach(f->{
            Map<String,Object> response = f.join();
            System.out.println(response);
        });

        return "done";
    }


}
