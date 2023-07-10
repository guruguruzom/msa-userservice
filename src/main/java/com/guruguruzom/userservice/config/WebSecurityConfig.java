package com.guruguruzom.userservice.config;

import com.guruguruzom.userservice.filter.AuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //권한 관련 config
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //http.authorizeHttpRequests().antMatchers("/user/**").permitAll();
        //모든 요청을 통과시키지 않음
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("192.168.35.46") //ip변경
                        .and()
                                .addFilter(getAuthentionFilter());


        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthentionFilter() throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }
}
