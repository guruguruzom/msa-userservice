package com.guruguruzom.userservice.controller;

import com.guruguruzom.userservice.dto.UserDto;
import com.guruguruzom.userservice.service.UserService;
import com.guruguruzom.userservice.vo.Greeting;
import com.guruguruzom.userservice.vo.RequestUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final Greeting greeting;

    private final UserService userService;

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

    @PostMapping("/users")
    public String createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);
        return "Create Completed";
    }
}
