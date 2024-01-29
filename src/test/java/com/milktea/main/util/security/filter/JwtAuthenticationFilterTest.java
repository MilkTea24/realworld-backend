package com.milktea.main.util.security.filter;

import com.milktea.main.factory.UserMother;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import com.milktea.main.util.security.BoardUserDetails;
import com.milktea.main.util.security.BoardUserDetailsService;
import com.milktea.main.util.security.filter.JwtAuthenticationFilter;
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

    private static User correctTestUser;

    private static final String TEST_SIGNING_KEY = "adsfasfasfasdfasdfasfasdfasfasdfasdfasfdasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasfd";
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3MDYxODYzNzksImVtYWlsIjoibmV3VXNlckBuYXZlci5jb20iLCJhdXRob3JpdGllcyI6IlVTRVIifQ.Tw-MfmOivJYkMZNbrDUaXCxAt9Rl7iTJhof--Q7kq7YlBoP4sbOgYVUnJTt41mREQkKGQRhbDIQdYGqy3wDsNw";

    //기존 Token signature에서 마지막 한글자를 a로 변경
    private static final String INVALID_SIGNATURE_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3MDYxODYzNzksImVtYWlsIjoibmV3VXNlckBuYXZlci5jb20iLCJhdXRob3JpdGllcyI6IlVTRVIifQ.Tw-MfmOivJYkMZNbrDUaXCxAt9Rl7iTJhof--Q7kq7YlBoP4sbOgYVUnJTt41mREQkKGQRhbDIQdYGqy3wDsNa";

    @BeforeEach
    void setup() {
        correctTestUser = UserMother.user().build();
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
        String email = result.getName();
        Assertions.assertEquals("newUser@naver.com", email);

        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        Assertions.assertEquals(1, authorities.size());

        Assertions.assertEquals("USER", authorities.toArray()[0].toString());
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
            if (!username.equals(correctTestUser.getUsername())) {
               throw new UsernameNotFoundException(
                        "인증 과정에서 문제가 발생하였습니다.");
            }

            return new BoardUserDetails(correctTestUser);
        }
    }
}