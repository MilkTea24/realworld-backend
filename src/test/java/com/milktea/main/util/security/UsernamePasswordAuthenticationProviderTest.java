package com.milktea.main.util.security;

import com.milktea.main.factory.UserMother;
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

    private static User correctTestUser;

    //userMother의 비밀번호와 일치하면 인증 성공하는 비밀번호임
    private static final String INPUT_PASSWORD = "12341234";

    private static MockBoardUserDetailsService userDetailsService;

    private static PasswordEncoder passwordEncoder;


    @BeforeEach
    void setup() {
        correctTestUser = UserMother.user().build();
        userDetailsService = new MockBoardUserDetailsService(null);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("올바른 자격 증명이 확인되면 AuthenticationToken을 반환한다.")
    void authenticate_success_test() {
        //given
        Authentication inputAuth = new UsernamePasswordAuthenticationToken(correctTestUser.getUsername(), INPUT_PASSWORD);
        UsernamePasswordAuthenticationProvider provider = new UsernamePasswordAuthenticationProvider(userDetailsService, passwordEncoder);

        //when
        Authentication outputAuth = provider.authenticate(inputAuth);

        //then
        Assertions.assertNotNull(outputAuth);
        Assertions.assertEquals("USER", outputAuth.getAuthorities().toArray()[0].toString());

    }

    @Test
    @DisplayName("올바르지 않은 비밀번호가 입력되면 BadCreditialsException이 발생한다.")
    void authenticate_fail_invalid_password_test() {
        User incorrectPasswordUser = UserMother.user().withPassword("1234512345", passwordEncoder).build();

        //given
        Authentication inputAuth = new UsernamePasswordAuthenticationToken(incorrectPasswordUser.getUsername(), incorrectPasswordUser.getPassword());
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
            if (!username.equals(correctTestUser.getUsername())) {
                throw new UsernameNotFoundException(
                        "인증 과정에서 문제가 발생하였습니다.");
            }

            return new BoardUserDetails(correctTestUser);
        }
    }
}
