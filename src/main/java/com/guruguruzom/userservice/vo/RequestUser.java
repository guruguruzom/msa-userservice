package com.guruguruzom.userservice.vo;


import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
//import javax

@Data
public class RequestUser {
    @NotNull(message = "Email connot be null")
    @Size(min = 2, message =  "Email not be less than two characters")
    private String email;

    @NotNull(message = "Name connot be null")
    @Size(min = 2, message =  "Name not be less than two characters")
    private String name;

    @NotNull(message = "password connot be null")
    @Size(min = 8, message =  "password not be less than two characters")
    private String pwd;
}
