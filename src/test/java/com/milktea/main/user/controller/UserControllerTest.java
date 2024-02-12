package com.milktea.main.user.controller;

import com.milktea.main.factory.MockSecurityContextFactory;
import com.milktea.main.factory.UserMother;
import com.milktea.main.factory.WithCustomUser;
import com.milktea.main.user.dto.response.UserInfoResponse;
import com.milktea.main.user.dto.response.UserLoginResponse;
import com.milktea.main.user.dto.response.UserRegisterResponse;
import com.milktea.main.user.dto.response.UserUpdateResponse;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.service.UserService;
import com.milktea.main.util.exceptions.GlobalExceptionHandler;
import com.milktea.main.util.exceptions.ValidationException;
import com.milktea.main.util.security.filter.InitialAuthenticationFilter;
import com.milktea.main.util.security.filter.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = UserRestController.class,
excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {InitialAuthenticationFilter.class, JwtAuthenticationFilter.class}) //내가 생성한 시큐리티 필터 생성하지 않도록(@Autowired 주입이 아니라 생성하면 에러 난다)
})
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
public class UserControllerTest {
    private MockMvc mockMvc;


    @MockBean
    private UserService mockUserService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserRestController(mockUserService))
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @DisplayName("회원 가입(POST /api/users)")
    @Nested
    class Register {
        private static final String REGISTER_URL = "/api/users";
        @Test
        @DisplayName("성공 테스트")
        void register_user_success_test() throws Exception {
            //given
            //입력 JSON 문자열
            String jsonString = """
                        {
                          "user":{
                            "username": "newUser",
                            "email": "newUser@naver.com",
                            "password": "12341234"
                          }
                        }
                    """;
            //출력할 객체 생성
            User user = UserMother.user().build();
            UserRegisterResponse registerResponse = new UserRegisterResponse(new UserRegisterResponse.UserRegisterDTO(user));

            //mock 정의
            when(mockUserService.registerUser(any())).thenReturn(registerResponse);

            //when
            final ResultActions actions = mockMvc.perform(
                    post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));


            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());

            actions
                    .andExpect(jsonPath("user.username").value("newUser"))
                    .andExpect(jsonPath("user.email").value("newUser@naver.com"));
        }

        @Test
        @DisplayName("username null 실패 테스트")
        void register_user_username_null_fail_test() throws Exception {
            //given
            //입력 JSON 문자열
            String jsonString = """
                    {
                      "user":{
                        "email": "newUser@naver.com",
                        "password": "12341234"
                      }
                    }
                    """;

            //출력할 객체 생성
            User user = UserMother.user().build();
            UserRegisterResponse registerResponse = new UserRegisterResponse(new UserRegisterResponse.UserRegisterDTO(user));

            //mock 정의
            when(mockUserService.registerUser(any())).thenReturn(registerResponse);

            //when
            final ResultActions actions = mockMvc.perform(
                    post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));

            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());
            actions.andExpect(jsonPath("errors.body").exists());
        }

        @Test
        @DisplayName("잘못된 이메일 형식 실패 테스트")
        void register_user_email_invalid_fail_test() throws Exception {
            //given
            //입력 JSON 문자열
            String jsonString = """
                    {
                      "user":{
                        "username": "newUser",
                        "email": "newUser",
                        "password": "12341234"
                      }
                    }
                    """;

            //출력할 객체 생성
            User user = UserMother.user().build();
            UserRegisterResponse registerResponse = new UserRegisterResponse(new UserRegisterResponse.UserRegisterDTO(user));

            //mock 정의
            when(mockUserService.registerUser(any())).thenReturn(registerResponse);

            //when
            final ResultActions actions = mockMvc.perform(
                    post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));

            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());
            actions.andExpect(jsonPath("errors.body").exists());
        }

        @Test
        @DisplayName("중복된 이메일 실패 테스트")
        void register_user_email_duplicate_fail_test() throws Exception {
            //given
            //입력 JSON 문자열
            String jsonString = """
                    {
                      "user":{
                        "username": "newUser",
                        "email": "newUser@naver.com",
                        "password": "12341234"
                      }
                    }
                    """;

            //mock 정의
            when(mockUserService.registerUser(any())).thenThrow(
                    new ValidationException(ValidationException.ErrorType.DUPLICATE_EMAIL, "email", "이미 존재하는 email입니다.")
            );

        //when
            final ResultActions actions = mockMvc.perform(
                    post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));

            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());
            actions.andExpect(jsonPath("errors.body").exists());
        }

        @Test
        @DisplayName("일반적인 예외 실패 테스트")
        void register_user_unknown_exception_fail_test() throws Exception {
            //given
            //입력 JSON 문자열
            String jsonString = """
                    {
                      "user":{
                        "username": "newUser",
                        "email": "newUser@naver.com",
                        "password": "12341234"
                      }
                    }
                    """;


            //mock 정의
            when(mockUserService.registerUser(any())).thenThrow(
                    new ArrayIndexOutOfBoundsException()
            );

            //when
            final ResultActions actions = mockMvc.perform(
                    post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));

            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());
            actions.andExpect(jsonPath("errors.body").exists());
        }

    }

    @Nested
    @DisplayName("로그인(POST /api/users/login)")
    class Login{
        private static final String LOGIN_URL = "/api/users/login";

        @Test
        @DisplayName("성공 테스트")
        void login_success_test() throws Exception {
            //given
            //입력 JSON 문자열
            String jsonString = """
                        {
                          "user":{
                            "email": "newUser@naver.com",
                            "password": "12341234"
                          }
                        }
                    """;
            //출력할 객체 생성
            User user = UserMother.user().build();
            UserLoginResponse loginResponse = new UserLoginResponse(new UserLoginResponse.UserLoginDTO(user));

            //mock 정의
            when(mockUserService.getLoginUser(any())).thenReturn(loginResponse);

            //when
            final ResultActions actions = mockMvc.perform(
                    post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));


            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());

            actions
                    .andExpect(jsonPath("user.username").value("newUser"))
                    .andExpect(jsonPath("user.email").value("newUser@naver.com"));
        }
    }

    @Nested
    @DisplayName("유저 정보 얻기(GET /api/user)")
    class GetUser{
        private static final String GET_USER_URL = "/api/user";
        @Test
        @DisplayName("성공 테스트")
        @WithCustomUser
        void get_user_success_test() throws Exception {
            //given

            //출력할 객체 생성
            User user = UserMother.user().build();
            UserInfoResponse response = new UserInfoResponse(new UserInfoResponse.UserInfoDTO(user, "test token"));

            //mock 정의
            when(mockUserService.getCurrentUser(any(), any())).thenReturn(response);

            //when
            final ResultActions actions = mockMvc.perform(
                    get(GET_USER_URL)
                            .header("Authentication", "test token"));


            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());

            actions
                    .andExpect(jsonPath("user.username").value("newUser"))
                    .andExpect(jsonPath("user.email").value("newUser@naver.com"));
        }
    }

    @Nested
    @DisplayName("유저 정보 업데이트하기(PUT /api/user)")
    class update {
        private static final String UPDATE_USER_URL = "/api/user";

        @Test
        @DisplayName("성공 테스트")
        @WithCustomUser
        void update_success_test() throws Exception {
            //given
            String jsonString = """
                        {
                          "user":{
                            "email": "newUser2@naver.com",
                            "bio": "update bio"
                          }
                        }
                    """;

            //출력할 객체 생성
            User user = UserMother.user()
                    .withBio("update bio")
                    .withEmail("newUser2@naver.com")
                    .build();
            UserUpdateResponse response = new UserUpdateResponse(new UserUpdateResponse.UserUpdateDTO(user, "new token"));

            //mock 정의
            when(mockUserService.updateUser(any(), any(), any())).thenReturn(response);

            //when
            final ResultActions actions = mockMvc.perform(
                    put(UPDATE_USER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));


            actions
                    .andExpect(jsonPath("user.bio").value("update bio"))
                    .andExpect(jsonPath("user.email").value("newUser2@naver.com"));
        }
    }
}
