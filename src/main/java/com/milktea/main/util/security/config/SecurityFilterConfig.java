package com.milktea.main.util.security.config;

import com.milktea.main.util.security.filter.ExceptionHandlingFilter;
import com.milktea.main.util.security.filter.InitialAuthenticationFilter;
import com.milktea.main.util.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
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
    private final ExceptionHandlingFilter exceptionHandlingFilter;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                //허용할 API 목록
                .requestMatchers(ALL_METHOD_WHITELIST).permitAll()
                .requestMatchers(SPECIFIC_METHOD_WHITELIST).permitAll()

                        //그 외 API는 모두 인증 필요
                .anyRequest().authenticated());



        //basicAuthenticationFilter와 같은 우선순위로 initalAuthenticationFilter 삽입
        http.addFilterAt(initialAuthenticationFilter, BasicAuthenticationFilter.class)
                //BasicAuthenticationFilter 다음 jwtAuthenticationFilter가 위치
                        .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                                .addFilterBefore(exceptionHandlingFilter, BasicAuthenticationFilter.class);


        //세션 대신 JWT 토큰 사용
        http.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    //h2-console URL 접근 위함
    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled",havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }
}
