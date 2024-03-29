package com.milktea.main.util.security.filter;


import com.milktea.main.util.security.EmailPasswordAuthentication;
import com.milktea.main.util.security.jwt.JwtTokenAdministrator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.milktea.main.util.security.jwt.JwtAuthenticationWhiteList.ALL_METHOD_WHITELIST;
import static com.milktea.main.util.security.jwt.JwtAuthenticationWhiteList.SPECIFIC_METHOD_WHITELIST;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenAdministrator jwtTokenAdministrator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");

        Claims claims = jwtTokenAdministrator.verifyToken(jwt);

        String email = String.valueOf(claims.get("email"));

        //하나의 String으로 되어있는 Claims.get("authorities")에 "AUTHORITY1, AUTHORITY2"를 분리하여 List<? extends GrantedAuthority>로 만든다.
        List<? extends GrantedAuthority> authorities = Arrays.stream(((String)claims.get("authorities")).split(JwtTokenAdministrator.AUTHORITY_DELIMITER))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken auth = new EmailPasswordAuthentication(
                email,
                null,
                authorities
        );

        //이 때 담은 auth는 호출된 메서드가 종료될 때 까지 유효하다 -> sessionless를 사용헀기 때문에 영속화는 안됨
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
