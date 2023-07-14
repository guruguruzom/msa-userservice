package com.guruguruzom.userservice.controller;

import com.guruguruzom.userservice.dto.UserDto;
import com.guruguruzom.userservice.entity.UserEntity;
import com.guruguruzom.userservice.service.UserService;
import com.guruguruzom.userservice.vo.Greeting;
import com.guruguruzom.userservice.vo.RequestUser;
import com.guruguruzom.userservice.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final Greeting greeting;

    private final UserService userService;

    @GetMapping("/health-check")
    public String status(){
        StringBuilder healthMessage = new StringBuilder("server check in user service on Port")
                .append(", port(local.server.port)=").append(env.getProperty("local.server.port"))
                .append(", port(server.port)=").append(env.getProperty("server.port"))
                .append(", token secret=").append(env.getProperty("token.secret"))
                .append(", token expiration time=").append(env.getProperty("token.expiration_time"));
        return healthMessage.toString();
    }

    @GetMapping("/welcome")
    public String welcome(){
        //env를 통한 방법
        env.getProperty("greeting.message");
        
        //component를 이용
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);


        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public  ResponseEntity<List<ResponseUser>> getUser(){
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v,ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public  ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser result = new ModelMapper().map(userDto, ResponseUser.class);


        return ResponseEntity.status(HttpStatus.OK).body(result);

    }
}
