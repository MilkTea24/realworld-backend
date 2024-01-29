package com.milktea.main.util.security;

import com.milktea.main.util.exceptions.ExceptionUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class LoginHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final String body;

    public LoginHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        body = copyRequestBodyToString(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException();
            }

            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };

        return servletInputStream;
    }

    private String copyRequestBodyToString(HttpServletRequest request) throws IOException {
        try {
            ServletInputStream servletInputStream = request.getInputStream();
            return StreamUtils.copyToString(servletInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("request의 body를 읽어들이던 중 문제가 발생하였습니다.");
            if (log.isDebugEnabled()) log.debug("위치 - {},\n 에러 stack\n - {}", this.getClass().getName(), ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }
}
