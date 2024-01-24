package com.milktea.main.util.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.milktea.main.util.security.JwtAuthenticationWhiteList.ALL_METHOD_WHITELIST;
import static com.milktea.main.util.security.JwtAuthenticationWhiteList.SPECIFIC_METHOD_WHITELIST;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.signing.key}")
    private final String signingKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");

        //서명된 Jwt를 풀기 위한 Secret Key 생성
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );

        //JWT에서 정보 얻기
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) //서명 검증을 위한 SecretKey 입력
                .build()
                .parseClaimsJws(jwt) //토큰이 유효한지 검사. 유효하지 않으면 여러 종류 예외 발생
                .getBody();

        String username = String.valueOf(claims.get("username"));

        //하나의 String으로 되어있는 Claims.get("authorities")에 "AUTHORITY1, AUTHORITY2"를 분리하여 List<? extends GrantedAuthority>로 만든다.
        List<? extends GrantedAuthority> authorities = Arrays.stream(((String)claims.get("authorities")).split(InitialAuthenticationFilter.AUTHORITY_DELIMITER))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthentication(
                username,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    //JWT 인증이 필요없는 요청에는 필터링하지 않도록
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestPath = request.getServletPath();

        //모든 Method를 허용하는 API는 antPathMatcher로 검증
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if (Arrays.stream(ALL_METHOD_WHITELIST).anyMatch(pattern -> antPathMatcher.match(pattern, requestPath))) return true;

        //특정 Method만 허용하는 API는 RegexRequestMatchers로 검증
        if (Arrays.stream(SPECIFIC_METHOD_WHITELIST).anyMatch(regexRequestMatcher -> regexRequestMatcher.matches(request))) return true;

        //그 외 모든 요청은 필터링해야 함
        return false;
    }
}
