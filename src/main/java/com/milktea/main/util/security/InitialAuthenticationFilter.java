package com.milktea.main.util.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager manager;

    @Value("${jwt.signing.key}")
    private final String signingKey;

    //"/api/users/login"의 요청을 이 필터가 가로챈다.
    //AuthenticationManager가 Authentication을 검증한다.
    //검증이 되면 Authorization Header에 jwt Token을 반환한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        log.debug("username: " + username + ", password: " + password);
        Authentication authentication = new UsernamePasswordAuthentication(username, password);
        manager.authenticate(authentication);

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );

        String jwt = Jwts.builder()
                .setClaims(Map.of("username", username)) //JWT와 관련된 정보 입력
                .signWith(key) //서명 키는 실제로 사용자별로 다른 키를 사용해야 한다.
                .compact();

        response.setHeader("Authorization", jwt);

        //filterChain.doFilter(request, response);
    }

    //"/api/users/login"에만 이 필터를 적용
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/api/users/login");
    }
}
