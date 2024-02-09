package com.milktea.main.util.security.filter;


import com.milktea.main.factory.UserMother;
import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import com.milktea.main.util.security.EmailPasswordAuthentication;
import com.milktea.main.util.security.jwt.JwtTokenAdministrator;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Slf4j
public class InitialAuthenticationFilterTest {
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockFilterChain filterChain;
    private static User correctTestUser;

    //Mock AuthenticationManager
    private static MockAuthenticationManager manager;

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
    @DisplayName("email과 password가 일치하면 jwt 토큰을 반환한다")
    void correct_username_password_test() throws ServletException, IOException {
        //given
        InitialAuthenticationFilter initialAuthenticationFilter = new InitialAuthenticationFilter(manager, new MockJwtTokenAdministrator());
        request.addHeader("content-type", "application/json");
        request.setContentType("application/json");
        request.setContent("""
                {
                  "user":{
                    "email": "newUser@naver.com",
                    "password": "12341234"
                  }
                }
                """.getBytes(StandardCharsets.UTF_8));
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

            String email = authentication.getName();
            String password = String.valueOf(authentication.getCredentials());

            if (!email.equals("newUser@naver.com") || !password.equals("12341234"))
                throw new BadCredentialsException("자격 증명이 잘못됨");

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities = correctTestUser.getAuthorities().stream()
                    .map(Authority::getName)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new EmailPasswordAuthentication(
                    email,
                    password,
                    authorities);
        }
    }

    private static class MockJwtTokenAdministrator extends JwtTokenAdministrator {
        public MockJwtTokenAdministrator() {
            super(null, null, null);
        }

        @Override
        public String issueToken(Authentication returnAuthentication) {

            log.debug(returnAuthentication.getName());
            return "test success token";
        }
    }
}
