package com.guruguruzom.userservice.config;

import com.guruguruzom.userservice.filter.AuthenticationFilter;
import com.guruguruzom.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    private final String IP_ADDRESS = "127.0.0.1";
    //권한 관련 config
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //http.authorizeHttpRequests().antMatchers("/user/**").permitAll();
        //모든 요청을 통과시키지 않음

        http.authorizeRequests()
                .antMatchers("/error/**").permitAll()
                .antMatchers("/**")
                .access("hasIpAddress('" + IP_ADDRESS + "')")
                .anyRequest().authenticated()
                        .and()
                                .addFilter(getAuthentionFilter());


        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthentionFilter() throws Exception{
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(),userService,env);
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    //인증에 관련된 작업
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //1.userDetailsService(userService) 사용자를 검색해온다
        //2.passwordEncoder
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
