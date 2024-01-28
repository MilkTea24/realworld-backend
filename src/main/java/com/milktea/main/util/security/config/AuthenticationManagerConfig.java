package com.milktea.main.util.security.config;

import com.milktea.main.util.security.EmailPasswordAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig {
    private final EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider;

    //AuthenticationManager Bean에 usernamePasswordAuthenticationProvider 연결한 후 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(emailPasswordAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }
}