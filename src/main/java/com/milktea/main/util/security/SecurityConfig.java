package com.milktea.main.util.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final InitialAuthenticationFilter initialAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        //basicAuthenticationFilter와 같은 우선순위로 initalAuthenticationFilter 삽입
        //BasicAuthenticationFilter 다음 jwtAuthenticationFilter가 위치
        http.addFilterAt(initialAuthenticationFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(authorize -> authorize
                //허용할 API 목록
                .requestMatchers("/api/users/login", "/api/tags", "/api/profiles/*").permitAll()
                .requestMatchers(new RegexRequestMatcher("/api/users", "POST"),
                        new RegexRequestMatcher("/api/articles", "GET"),
                        new RegexRequestMatcher("/api/articles/*", "GET"),
                        new RegexRequestMatcher("/api/articles/*/comments", "GET")).permitAll()
                //그 외 API는 모두 인증 필요
                .anyRequest().authenticated());

        //세션 대신 JWT 토큰 사용
        http.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));



        return http.build();
    }


}
