package com.milktea.main.user.service;

import com.milktea.main.factory.UserMother;
import com.milktea.main.factory.WithCustomUser;
import com.milktea.main.user.dto.request.UserInfoDTO;
import com.milktea.main.user.dto.request.UserLoginRequest;
import com.milktea.main.user.dto.request.UserRegisterRequest;
import com.milktea.main.user.dto.request.UserUpdateRequest;
import com.milktea.main.user.dto.response.UserInfoResponse;
import com.milktea.main.user.dto.response.UserLoginResponse;
import com.milktea.main.user.dto.response.UserRegisterResponse;
import com.milktea.main.user.dto.response.UserUpdateResponse;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.AuthorityRepository;
import com.milktea.main.user.repository.UserRepository;
import com.milktea.main.util.exceptions.ValidationException;
import com.milktea.main.util.security.EmailPasswordAuthentication;
import com.milktea.main.util.security.jwt.JwtTokenAdministrator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
public class UserServiceTest {
    private static User user;

    private static UserService userService;

    private static UserRepository userRepository;

    private static AuthorityRepository authorityRepository;

    private static PasswordEncoder passwordEncoder;

    private static JwtTokenAdministrator jwtTokenAdministrator;



    @BeforeEach
    void setup() {
        user = UserMother.user().build();
        userRepository = Mockito.mock(UserRepository.class);
        authorityRepository = Mockito.mock(AuthorityRepository.class);
        jwtTokenAdministrator = Mockito.mock(JwtTokenAdministrator.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(passwordEncoder, userRepository, authorityRepository, jwtTokenAdministrator);
    }

    @Nested
    @DisplayName("회원 가입(POST /api/users)")
    class Register {
        @Test
        @DisplayName("성공 테스트")
        void register_user_success_test() {
            //given
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
            UserRegisterRequest.UserRegisterDTO userRegisterRequest = new UserRegisterRequest.UserRegisterDTO(user);

            //when
            when(userRepository.findByUsername(eq("newUser"))).thenReturn(Optional.of(user)); //중복된 username 있음
            when(userRepository.save(any())).thenReturn(user);

            //then
            Assertions.assertThrows(ValidationException.class, () -> userService.registerUser(userRegisterRequest));
        }

        @Test
        @DisplayName("중복된 email 실패 테스트")
        void register_user_duplicate_email_fail_test() {
            //given
            UserRegisterRequest.UserRegisterDTO userRegisterRequest = new UserRegisterRequest.UserRegisterDTO(user);

            //when
            when(userRepository.findByEmail(eq("newUser@naver.com"))).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenReturn(user);

            //then
            Assertions.assertThrows(ValidationException.class, () -> userService.registerUser(userRegisterRequest));
        }
    }

    @Nested
    @DisplayName("로그인(POST /api/users/login)")
    class Login {
        @Test
        @DisplayName("성공 테스트")
        void login_user_success_test() {
            //given
            UserLoginRequest.UserLoginDTO userLoginRequest = new UserLoginRequest.UserLoginDTO(user);

            //when
            when(userRepository.findByEmail(eq("newUser@naver.com"))).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenReturn(user);
            UserLoginResponse result = userService.getLoginUser(userLoginRequest);


            //then
            Assertions.assertEquals("newUser", result.userLoginDTO().username());
        }
    }

    @Nested
    @DisplayName("유저 정보 얻기(GET /api/user)")
    class GetUser {
        @Test
        @DisplayName("성공 테스트")
        void get_user_success_test() {
            //given
            UserInfoDTO userInfoRequest = new UserInfoDTO(user);

            //when
            when(userRepository.findByEmail(eq("newUser@naver.com"))).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenReturn(user);
            UserInfoResponse result = userService.getCurrentUser(userInfoRequest, "test token");


            //then
            Assertions.assertEquals("newUser", result.userInfoDTO().username());
        }
    }

    @Nested
    @DisplayName("유저 정보 업데이트하기(PUT /api/user)")
    class Update {
        private static Authentication auth;
        @BeforeEach
        void setup() {
            auth = new EmailPasswordAuthentication(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities().stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                            .toList()
            );
        }

        @Test
        @DisplayName("성공 테스트")
        void update_user_success_test() {
            //given
            UserUpdateRequest.UserUpdateDTO userUpdateRequest = new UserUpdateRequest.UserUpdateDTO(null, "newUser2@naver.com", "update bio", null, null);


            //when
            when(userRepository.findByEmail(eq("newUser@naver.com"))).thenReturn(Optional.of(user));
            when(userRepository.findByEmail(eq("newUser2@naver.com"))).thenReturn(Optional.empty());
            when(jwtTokenAdministrator.updateToken(any(), any(), any())).thenReturn("new Token");
            UserUpdateResponse result = userService.updateUser(auth, userUpdateRequest, "old Token");

            //then
            Assertions.assertEquals("newUser2@naver.com", result.userUpdateDTO().email());
            Assertions.assertEquals("update bio", result.userUpdateDTO().bio());
            Assertions.assertEquals("new Token", result.userUpdateDTO().token());
        }

        @Test
        @DisplayName("중복된 이메일 실패 테스트")
        void update_user_duplicate_email_fail_test() {
            //given
            UserUpdateRequest.UserUpdateDTO userUpdateRequest = new UserUpdateRequest.UserUpdateDTO(null, "newUser2@naver.com", "update bio", null, null);
            Authentication auth = new EmailPasswordAuthentication(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities().stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                            .toList()
            );

            //when
            //then
            when(userRepository.findByEmail(eq("newUser@naver.com"))).thenReturn(Optional.of(user));
            when(userRepository.findByEmail(eq("newUser2@naver.com"))).thenReturn(Optional.of(UserMother.user()
                    .withEmail("newUser2@naver.com")
                    .build()));
            when(jwtTokenAdministrator.updateToken(any(), any(), any())).thenReturn("new Token");

            Assertions.assertThrows(ValidationException.class, () -> userService.updateUser(auth, userUpdateRequest, "old Token"));
        }

        @Test
        @DisplayName("중복된 username 실패 테스트")
        void update_user_duplicate_username_fail_test() {
            //given
            UserUpdateRequest.UserUpdateDTO userUpdateRequest = new UserUpdateRequest.UserUpdateDTO("newUser2", null, "update bio", null, null);
            Authentication auth = new EmailPasswordAuthentication(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities().stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                            .toList()
            );

            //when
            //then
            when(userRepository.findByEmail(eq("newUser@naver.com"))).thenReturn(Optional.of(user));
            when(userRepository.findByUsername(eq("newUser2"))).thenReturn(Optional.of(UserMother.user()
                    .withUsername("newUser2")
                    .build()));
            when(jwtTokenAdministrator.updateToken(any(), any(), any())).thenReturn("new Token");

            Assertions.assertThrows(ValidationException.class, () -> userService.updateUser(auth, userUpdateRequest, "old Token"));
        }
    }

}
