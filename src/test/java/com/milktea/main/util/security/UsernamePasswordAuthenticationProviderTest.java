package com.milktea.main.util.security;

import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class UsernamePasswordAuthenticationProviderTest {

    private static final String TEST_USERNAME = "newUser";

    private static final String TEST_CORRECT_PASSWORD = "12341234";
    private static final String TEST_INCORRECT_PASSWORD = "1234512345";
    private static final String TEST_USER_AUTHORITY = "USER";


    private static MockBoardUserDetailsService userDetailsService;

    private static PasswordEncoder passwordEncoder;


    @BeforeEach
    void setup() {
        userDetailsService = new MockBoardUserDetailsService(null);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("올바른 자격 증명이 확인되면 AuthenticationToken을 반환한다.")
    void authenticate_success_test() {
        //given
        Authentication inputAuth = new UsernamePasswordAuthenticationToken(TEST_USERNAME, TEST_CORRECT_PASSWORD);
        UsernamePasswordAuthenticationProvider provider = new UsernamePasswordAuthenticationProvider(userDetailsService, passwordEncoder);

        //when
        Authentication outputAuth = provider.authenticate(inputAuth);

        //then
        Assertions.assertNotNull(outputAuth);
        Assertions.assertEquals(TEST_USER_AUTHORITY, String.valueOf(outputAuth.getAuthorities().toArray()[0]));
    }

    @Test
    @DisplayName("올바르지 않은 비밀번호가 입력되면 BadCreditialsException이 발생한다.")
    void authenticate_fail_invalid_password_test() {
        //given
        Authentication inputAuth = new UsernamePasswordAuthenticationToken(TEST_USERNAME, TEST_INCORRECT_PASSWORD);
        UsernamePasswordAuthenticationProvider provider = new UsernamePasswordAuthenticationProvider(userDetailsService, passwordEncoder);

        //when
        Assertions.assertThrows(BadCredentialsException.class, () -> provider.authenticate(inputAuth));
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
            User user = User.builder().username(username).password(passwordEncoder.encode(TEST_CORRECT_PASSWORD)).build();
            authority.setUser(user);
            return new BoardUserDetails(user);
        }
    }
}
