package com.milktea.main.util.security;

import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Collection;

@Slf4j
public class JwtAuthenticationFilterTest {
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockFilterChain filterChain;
    private static MockBoardUserDetailsService boardUserDetailsService;


    private static final String TEST_USERNAME = "newUser";
    private static final String TEST_PASSWORD = "12341234";
    private static final String TEST_USER_AUTHORITY = "USER";
    private static final String TEST_SIGNING_KEY = "adsfasfasfasdfasdfasfasdfasfasdfasdfasfdasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasfd";
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3MDQ5ODMyMzEsInVzZXJuYW1lIjoibmV3VXNlciIsImF1dGhvcml0aWVzIjoiVVNFUiJ9.NnhHsW_Uf8Zitym7x0_AUEznEtRYOMLKB8VwknuGw3uJgVuUDIRmx-Vt3nYiWBUea87SpWq4fKDCFDonMKZpqw";

    //기존 Token signature에서 마지막 한글자를 a로 변경
    private static final String INVALID_SIGNATURE_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3MDQ5ODMyMzEsInVzZXJuYW1lIjoibmV3VXNlciIsImF1dGhvcml0aWVzIjoiVVNFUiJ9.NnhHsW_Uf8Zitym7x0_AUEznEtRYOMLKB8VwknuGw3uJgVuUDIRmx-Vt3nYiWBUea87SpWq4fKDCFDonMKZpqa";

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        boardUserDetailsService = new MockBoardUserDetailsService(null);
    }

    @Test
    @DisplayName("올바른 토큰을 입력하면 인증을 받을 수 있다.")
    void correct_token_authentication_test() throws ServletException, IOException {
        //given
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(TEST_SIGNING_KEY);
        request.addHeader("Authorization", TOKEN);

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        Authentication result = SecurityContextHolder.getContext().getAuthentication();
        String username = result.getName();
        Assertions.assertEquals(TEST_USERNAME, username);

        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        Assertions.assertEquals(1, authorities.size());

        for (GrantedAuthority a : authorities) {
            Assertions.assertEquals(TEST_USER_AUTHORITY, a.getAuthority());
        }
    }

    @Test
    @DisplayName("서명이 올바르지 않은 토큰을 입력하면 SignatureException이 발생한다")
    void incorrect_token_authentication_test() throws ServletException, IOException {
        //given
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(TEST_SIGNING_KEY);
        request.addHeader("Authorization", INVALID_SIGNATURE_TOKEN);

        //when
        //then
        Assertions.assertThrows(SignatureException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
    }

    private static class MockBoardUserDetailsService extends BoardUserDetailsService {
        public MockBoardUserDetailsService(UserRepository userRepository) {
            super(userRepository);
        }

        @Override
        public BoardUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            if (!username.equals(TEST_USERNAME)) {
               throw new UsernameNotFoundException(
                        "인증 과정에서 문제가 발생하였습니다.");
            }

            Authority authority = new Authority(TEST_USER_AUTHORITY);
            User user = User.builder().username(username).build();
            authority.setUser(user);
            return new BoardUserDetails(user);
        }
    }
}
