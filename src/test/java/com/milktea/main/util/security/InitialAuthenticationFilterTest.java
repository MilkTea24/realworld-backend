package com.milktea.main.util.security;


import com.milktea.main.factory.UserMother;
import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.*;


@Slf4j
public class InitialAuthenticationFilterTest {
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockFilterChain filterChain;
    private static User correctTestUser;

    //Mock AuthenticationManager
    private static MockAuthenticationManager manager;
    private static final String TEST_SIGNING_KEY = "adsfasfasfasdfasdfasfasdfasfasdfasdfasfdasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasfd";

    @BeforeEach
    void setup() {
        //Mockito.mock 말고 아래 사용
        correctTestUser = UserMother.user().build();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        manager = new MockAuthenticationManager();
    }

    @Test
    @DisplayName("username과 password가 일치하면 jwt 토큰을 반환한다")
    void correct_username_password_test() throws ServletException, IOException {
        //given
        InitialAuthenticationFilter initialAuthenticationFilter = new InitialAuthenticationFilter(manager, TEST_SIGNING_KEY);
        request.addHeader("username", correctTestUser.getUsername());
        request.addHeader("password", correctTestUser.getPassword());
        //when
        initialAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        String token = response.getHeader("Authorization");
        log.debug("token - {}", token);
        Assertions.assertNotNull(token);
    }

    private static class MockAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {

            String username = authentication.getName();
            String password = String.valueOf(authentication.getCredentials());

            if (!username.equals(correctTestUser.getUsername()) || !password.equals(correctTestUser.getPassword()))
                throw new BadCredentialsException("자격 증명이 잘못됨");

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities = correctTestUser.getAuthorities().stream()
                    .map(Authority::getName)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    authorities);
        }
    }
}
