package com.milktea.main.util.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.http.HttpResponse;

@Slf4j
public class ExceptionHandlingFilterTest {
    private static MockFilterChain filterChain;
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;

    private static ExceptionHandlingFilter exceptionHandlingFilter;

    private static MockThrowingExceptionFilter mockFilter;

    @BeforeEach
    void setup() {
        exceptionHandlingFilter = new ExceptionHandlingFilter();
        mockFilter = new MockThrowingExceptionFilter();
        filterChain = new MockFilterChain(new DispatcherServlet(), exceptionHandlingFilter, mockFilter);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("필터 체인에서 예외 발생 시 response 정상 출력 테스트")
    void filterChain_exception_print_response_test() throws ServletException, IOException {
        //given

        //when
        exceptionHandlingFilter.doFilterInternal(request, response, filterChain);

        //then
        Assertions.assertEquals("{\"errors\":{\"body\":[\"Test Exception\"]}}",response.getContentAsString());
    }

    private static class MockThrowingExceptionFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            throw new IOException("Test Exception");
        }
    }
}
