package com.guruguruzom.userservice.controller;

import com.guruguruzom.userservice.vo.Greeting;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final Greeting greeting;

    @GetMapping("/health-check")
    public String status(){
        return "server check";
    }

    @GetMapping("/welcome")
    public String welcome(){
        //env를 통한 방법
        env.getProperty("greeting.message");
        
        //component를 이용
        return greeting.getMessage();
    }
}
