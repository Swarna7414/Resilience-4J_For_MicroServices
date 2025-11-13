package com.Resilience4.Fourj.Controller;

import com.Resilience4.Fourj.Service.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class ResTControllerAPI {

    @Autowired
    private final UserServiceClient userServiceClient;


    public ResTControllerAPI(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }


    @GetMapping("/hita")
    public ResponseEntity<Map<String, Object>> hitA () throws InterruptedException {
        return new ResponseEntity<>(userServiceClient.hitServiceA(), HttpStatus.OK);
    }

    @PostMapping("/posta")
    public ResponseEntity<Map> hittwoA(@RequestParam String message){
        Map<String, Object> map = userServiceClient.hitServiceAWithPost(message).join();
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    @GetMapping("/getB")
    public ResponseEntity<String> hitA(@RequestParam String name){
        return new ResponseEntity<>(userServiceClient.hitServiceB(name),HttpStatus.OK);
    }

    @PostMapping("/postB")
    public ResponseEntity<Map> hitB(@RequestParam String name, @RequestParam Integer age){
        return new ResponseEntity<>(userServiceClient.hitWithParam(name, age),HttpStatus.OK);
    }

}
