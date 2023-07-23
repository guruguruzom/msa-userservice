package com.guruguruzom.userservice.service;

import com.guruguruzom.userservice.client.OrderServiceClient;
import com.guruguruzom.userservice.dto.UserDto;
import com.guruguruzom.userservice.entity.UserEntity;
import com.guruguruzom.userservice.repository.UserRepository;
import com.guruguruzom.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    //BCryptPasswordEncoder는 선언 된 적이 없으므로 UserServiceApplication에 시작 시 등록
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final Environment env;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    //private final RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if(userEntity == null){
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true,true,true,true,
                new ArrayList<>());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        System.out.println(userDto.getUserId());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //자료형이 딱맞아 떨어져야 작동하도록
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

//    @Override
//    public UserDto getUserByUserId(String userId) {
//        UserEntity userEntity = userRepository.findByUserId(userId);
//
//        if(userEntity == null)
//            throw  new UsernameNotFoundException("user not found");
//
//        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
//
//        List<ResponseOrder> orders = new ArrayList<>();
//        userDto.setOrders(orders);
//
//        return userDto;
//    }
@Override
public UserDto getUserByUserId(String userId) {
    UserEntity userEntity = userRepository.findByUserId(userId);

    if(userEntity == null)
        throw  new UsernameNotFoundException("user not found");

    UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

//    try{
//        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
//        userDto.setOrders(orderList);
//    } catch (FeignException ex){
//        log.error(ex.getMessage());
//    }

    /* ErrorDecorder */
    //List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

    /* CircuitBreaker */
    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
    List<ResponseOrder> orderList = circuitBreaker.run(
            () -> orderServiceClient.getOrders(userId),
            throwable -> new ArrayList<>());

    userDto.setOrders(orderList);

    return userDto;
}

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null)
            throw  new UsernameNotFoundException("user name not found");

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }
}
