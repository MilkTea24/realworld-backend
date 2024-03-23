package com.milktea.main.util.security.jwt;

import com.milktea.main.factory.UserMother;
import com.milktea.main.util.exceptions.JwtAuthenticationException;
import com.milktea.main.util.security.BoardUserDetails;
import com.milktea.main.util.security.EmailPasswordAuthentication;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAdministratorTest {
    private JwtTokenAdministrator jwtTokenAdministrator;

    private JwtTokenBlackListRepository jwtTokenBlackListRepository;

    private final String signingKey = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

    private BoardUserDetails userDetails;

    private Clock clock;

    private String token;

    @BeforeEach
    void setup() {
        /*
        clock = Clock.fixed(
                Instant.parse("2024-01-01T10:00:00Z"),
                ZoneOffset.ofHours(9)); //seoul;
         */
        clock = Mockito.mock(Clock.class);
        userDetails = new BoardUserDetails(UserMother.user().build());
        jwtTokenBlackListRepository = Mockito.mock(JwtTokenBlackListRepository.class);
        jwtTokenAdministrator = new JwtTokenAdministrator(signingKey, jwtTokenBlackListRepository, clock);
    }

    @Nested
    @DisplayName("토큰 발급")
    class issue {
        @Test
        @DisplayName("성공 테스트")
        void issue_token_success_test() {
            //given
            Authentication auth = new EmailPasswordAuthentication(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            //Clock Mock
            Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-01-01T10:00:00Z"));

            //when
            String token = jwtTokenAdministrator.issueToken(auth);

            //then
            log.debug(token);
            Assertions.assertNotNull(token);
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class verify {

        @Test
        @DisplayName("성공 테스트")
        void verify_token_success_test() throws ServletException {
            //given
            //expired time이 "2024-01-01T10:00:00Z"이고 test User일 때 토큰
            token = "Token eyJhbGciOiJIUzM4NCJ9." +
                    "eyJleHAiOjE3MDQxMDUwMDAsImVtYWlsIjoibmV3VXNlckBuYXZlci5jb20iLCJhdXRob3JpdGllcyI6IlVTRVIifQ." +
                    "EsW_BcUyYIhPwSftKcqp_8vyX20LnRw-gB6shFG21MJ9Nx7QK_1kDBCEGA3X48rf";

            //when
            Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-01-01T10:00:00Z"));
            Mockito.when(jwtTokenBlackListRepository.findById(any())).thenReturn(Optional.empty());
            Claims claims = jwtTokenAdministrator.verifyToken(token);

            //then
            Assertions.assertEquals("newUser@naver.com", claims.get("email", String.class));
        }

        @Test
        @DisplayName("토큰 만료 실패 테스트")
        void verify_token_expired_fail_test() throws ServletException {
            //given
            //expired time이 "2024-01-01T10:00:00Z"이고 test User일 때 토큰
            token = "Token eyJhbGciOiJIUzM4NCJ9." +
                    "eyJleHAiOjE3MDQxMDUwMDAsImVtYWlsIjoibmV3VXNlckBuYXZlci5jb20iLCJhdXRob3JpdGllcyI6IlVTRVIifQ." +
                    "EsW_BcUyYIhPwSftKcqp_8vyX20LnRw-gB6shFG21MJ9Nx7QK_1kDBCEGA3X48rf";

            //현재 시간 변경(생성 시간은 2024-01-01T10:00:00Z 현재 시간은 이로부터 한시간 뒤)
            Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-01-01T11:00:00Z"));

            //when
            //then
            Assertions.assertThrows(JwtAuthenticationException.class, () -> jwtTokenAdministrator.verifyToken(token));
        }

        @Test
        @DisplayName("토큰 잘못된 서명 실패 테스트")
        void verify_token_invalid_signature_fail_test() throws ServletException {
            //given
            //expired time이 "2024-01-01T10:00:00Z"이고 test User일 때 토큰에서 서명 수정 없이 payload의 이메일만 변경
            token = "Token eyJhbGciOiJIUzM4NCJ9." +
                    "eyJleHAiOjE3MDQxMDUwMDAsImVtYWlsIjoibmV3VXNlcjJAbmF2ZXIuY29tIiwiYXV0aG9yaXRpZXMiOiJVU0VSIn0." +
                    "EsW_BcUyYIhPwSftKcqp_8vyX20LnRw-gB6shFG21MJ9Nx7QK_1kDBCEGA3X48rf";

            //when
            Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-01-01T10:00:00Z"));

            //then
            Assertions.assertThrows(JwtAuthenticationException.class, () -> jwtTokenAdministrator.verifyToken(token));
        }

        @Test
        @DisplayName("잘못된 토큰 형식 실패 테스트")
        void verify_token_invalid_format_fail_test() throws ServletException {
            //given
            token = "Token test token";

            //when
            Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-01-01T10:00:00Z"));

            //then
            Assertions.assertThrows(JwtAuthenticationException.class, () -> jwtTokenAdministrator.verifyToken(token));
        }

        @Test
        @DisplayName("토큰 블랙리스트 실패 테스트")
        void verify_token_blacklist_fail_test() throws ServletException {
            //given
            //expired time이 "2024-01-01T10:00:00Z"이고 test User일 때 토큰
            token = "Token eyJhbGciOiJIUzM4NCJ9." +
                    "eyJleHAiOjE3MDQxMDUwMDAsImVtYWlsIjoibmV3VXNlckBuYXZlci5jb20iLCJhdXRob3JpdGllcyI6IlVTRVIifQ." +
                    "EsW_BcUyYIhPwSftKcqp_8vyX20LnRw-gB6shFG21MJ9Nx7QK_1kDBCEGA3X48rf";

            //when
            Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-01-01T10:00:00Z"));
            Mockito.when(jwtTokenBlackListRepository.findById(any())).thenReturn(Optional.of(new JwtTokenBlackList(
                    userDetails.getUsername(),
                    token.replaceFirst("Token ", "")
                    )));

            //then
            Assertions.assertThrows(BadCredentialsException.class, () -> jwtTokenAdministrator.verifyToken(token));
        }
    }

    @Nested
    @DisplayName("토큰 업데이트")
    class update {
        @Test
        @DisplayName("성공 테스트")
        void update_token_success_test() {
            //given
            token = "Token eyJhbGciOiJIUzM4NCJ9." +
                    "eyJleHAiOjE3MDQxMDUwMDAsImVtYWlsIjoibmV3VXNlckBuYXZlci5jb20iLCJhdXRob3JpdGllcyI6IlVTRVIifQ." +
                    "EsW_BcUyYIhPwSftKcqp_8vyX20LnRw-gB6shFG21MJ9Nx7QK_1kDBCEGA3X48rf";

            Authentication auth = new EmailPasswordAuthentication(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            String newEmail = "newUser2@naver.com";

            //when
            Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-01-01T10:00:00Z"));
            String newToken = jwtTokenAdministrator.updateToken(auth, newEmail, token);

            //then
            Assertions.assertNotEquals(newToken, token);
            log.debug(newToken);
        }
    }
}
