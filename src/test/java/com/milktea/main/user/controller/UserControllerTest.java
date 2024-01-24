package com.milktea.main.user.controller;

import com.milktea.main.factory.UserMother;
import com.milktea.main.user.dto.UserRegisterResponse;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.service.UserService;
import com.milktea.main.util.exceptions.GlobalExceptionHandler;
import com.milktea.main.util.security.InitialAuthenticationFilter;
import com.milktea.main.util.security.JwtAuthenticationFilter;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                    post("/api/users")
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
                    post("/api/users")
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
                    post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonString));

            //then
            log.debug("출력 JSON - {}", actions.andReturn().getResponse().getContentAsString());
            actions.andExpect(jsonPath("errors.body").exists());
        }
    }
}
