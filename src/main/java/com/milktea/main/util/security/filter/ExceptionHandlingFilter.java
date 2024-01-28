package com.milktea.main.util.security.filter;

import com.google.gson.Gson;
import com.milktea.main.util.exceptions.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Slf4j
@Component
public class ExceptionHandlingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = createErrorResponse(e, response);
            setResponseBody(response, errorResponse);
        }
    }

    private ErrorResponse createErrorResponse(Exception e, HttpServletResponse response) {
        ErrorResponse errorResponse = new ErrorResponse(
                new ErrorResponse.Errors(
                        List.of(e.getMessage())
                )
        );

        return errorResponse;
    }

    private void setResponseBody(HttpServletResponse response, ErrorResponse errorResponse) {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream())) {
            String jsonString = new Gson().toJson(errorResponse, ErrorResponse.class);
            outputStreamWriter.write(jsonString);
        } catch (IOException e) {
            log.error("response body를 작성할 수 없습니다.");
            if (log.isDebugEnabled()) log.debug("위치 - {}, error stack - {}", this.getClass().getName(), e.getStackTrace());
        }
    }
}
