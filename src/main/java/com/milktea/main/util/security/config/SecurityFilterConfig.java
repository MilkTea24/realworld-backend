package com.milktea.main.util.security.config;

import com.milktea.main.util.security.InitialAuthenticationFilter;
import com.milktea.main.util.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.milktea.main.util.security.JwtAuthenticationWhiteList.ALL_METHOD_WHITELIST;
import static com.milktea.main.util.security.JwtAuthenticationWhiteList.SPECIFIC_METHOD_WHITELIST;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterConfig {
    private final InitialAuthenticationFilter initialAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                //허용할 API 목록
                .requestMatchers(ALL_METHOD_WHITELIST).permitAll()
                .requestMatchers(SPECIFIC_METHOD_WHITELIST).permitAll()

                        //그 외 API는 모두 인증 필요
                .anyRequest().authenticated())
                //BasicAuthenticationFilter 다음 jwtAuthenticationFilter가 위치
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

        //basicAuthenticationFilter와 같은 우선순위로 initalAuthenticationFilter 삽입
        http.addFilterAt(initialAuthenticationFilter, BasicAuthenticationFilter.class);

        //세션 대신 JWT 토큰 사용
        http.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(ALL_METHOD_WHITELIST)
                .requestMatchers(SPECIFIC_METHOD_WHITELIST);
    }
}
