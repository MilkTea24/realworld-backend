package com.milktea.main.util.security.filter;

import com.google.gson.Gson;
import com.milktea.main.user.dto.request.UserLoginRequest;
import com.milktea.main.util.exceptions.ExceptionUtils;
import com.milktea.main.util.security.EmailPasswordAuthentication;
import com.milktea.main.util.security.LoginHttpServletRequestWrapper;
import com.milktea.main.util.security.jwt.JwtTokenAdministrator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager manager;

    private final JwtTokenAdministrator jwtTokenAdministrator;


    //"/api/users/login"의 요청을 이 필터가 가로챈다.
    //AuthenticationManager가 Authentication을 검증한다.
    //검증이 되면 Authorization Header에 jwt Token을 반환한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            LoginHttpServletRequestWrapper loginRequestWrapper = new LoginHttpServletRequestWrapper(request);
            String body = "";
            try {
                StringBuilder stringBuilder = getRequestToStringBuilder(loginRequestWrapper, response, filterChain);
                body = stringBuilder.toString();
            } catch (IOException e) {
                log.error("request Body 변환 과정에서 error 발생함! - {}", e.getMessage());
                if (log.isDebugEnabled()) log.debug("error stack - {}", ExceptionUtils.getStackTrace(e));
                //ExceptionHandlingFilter가 처리할 것
                throw new IOException("request body를 가져오는 중 문제가 발생했습니다.");
            }

            UserLoginRequest loginRequest = new Gson().fromJson(body, UserLoginRequest.class);
            String email = loginRequest.userLoginDTO().email();
            String password = loginRequest.userLoginDTO().password();

            Authentication authentication = new EmailPasswordAuthentication(email, password);
            Authentication returnAuthentication = manager.authenticate(authentication);

            String jwt = jwtTokenAdministrator.issueToken(returnAuthentication);

            response.setHeader("Authorization", jwt);

            filterChain.doFilter(loginRequestWrapper, response);
    }

    private StringBuilder getRequestToStringBuilder(LoginHttpServletRequestWrapper loginRequestWrapper, HttpServletResponse response, FilterChain filterChain) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(loginRequestWrapper.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line = "";
            while (!Objects.isNull(line = bufferedReader.readLine())) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            log.error("request body를 불러오는 중 문제가 발생하였습니다.");
            if (log.isDebugEnabled()) log.debug("위치 - {}\n 에러 stack - {}", this.getClass().getName(), ExceptionUtils.getStackTrace(e));
            throw new IOException(String.format("Request Body를 가져올 수 없습니다"));
        }

        return stringBuilder;
    }

    //"/api/users/login"에만 이 필터를 적용
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/api/users/login");
    }
}
