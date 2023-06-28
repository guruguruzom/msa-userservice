package com.guruguruzom.userservice.service;

import com.guruguruzom.userservice.dto.UserDto;
import com.guruguruzom.userservice.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
