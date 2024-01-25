package com.milktea.main.user.service;

import com.milktea.main.factory.UserMother;
import com.milktea.main.user.dto.UserRegisterRequest;
import com.milktea.main.user.dto.UserRegisterResponse;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.AuthorityRepository;
import com.milktea.main.user.repository.UserRepository;
import com.milktea.main.util.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
public class UserServiceTest {
    private static User user;

    private static UserRepository userRepository;

    private static AuthorityRepository authorityRepository;

    private static PasswordEncoder passwordEncoder;


    @BeforeEach
    void setup() {
        user = UserMother.user().build();
        userRepository = Mockito.mock(UserRepository.class);
        authorityRepository = Mockito.mock(AuthorityRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Nested
    @DisplayName("회원 가입(POST /api/users)")
    class Register {
        @Test
        @DisplayName("성공 테스트")
        void register_user_success_test() {
            //given
            UserService userService = new UserService(passwordEncoder, userRepository, authorityRepository);
            UserRegisterRequest.UserRegisterDTO userRegisterRequest = new UserRegisterRequest.UserRegisterDTO(user);

            //when
            when(userRepository.findByUsername(eq("newUser"))).thenReturn(Optional.empty()); //중복된 username 없음
            when(userRepository.save(any())).thenReturn(user);
            UserRegisterResponse result = userService.registerUser(userRegisterRequest);


            //then
            Assertions.assertEquals("newUser", result.userRegisterDTO().username());
        }

        @Test
        @DisplayName("중복된 username 실패 테스트")
        void register_user_duplicate_username_fail_test() {
            //given
            UserService userService = new UserService(passwordEncoder, userRepository, authorityRepository);
            UserRegisterRequest.UserRegisterDTO userRegisterRequest = new UserRegisterRequest.UserRegisterDTO(user);

            //when
            when(userRepository.findByUsername(eq("newUser"))).thenReturn(Optional.of(user)); //중복된 username 있음
            when(userRepository.save(any())).thenReturn(user);

            //then
            Assertions.assertThrows(ValidationException.class, () -> userService.registerUser(userRegisterRequest));
        }
    }





}
