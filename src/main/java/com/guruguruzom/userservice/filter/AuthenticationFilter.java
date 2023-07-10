package com.guruguruzom.userservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guruguruzom.userservice.vo.RequestLogin;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //요청정보 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            //post 형태로 전달되는것은 request pram으로 받을 수 없기 때문에 input stream으로 처리
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            //인증처리를 위한 Authenticatio manager 호출
            return getAuthenticationManager().authenticate(
                    //security에서 사용 할 수 있는 UsernamePasswordAuthenticationToken로 변경
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );



        } catch (IOException e){
            throw  new RuntimeException(e);
        }

    }

    //로그인을 성공 했을때 어떤 처리를 해줄것인지 정의
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

    }
}
