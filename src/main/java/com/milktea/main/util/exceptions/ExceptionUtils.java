package com.milktea.main.util.exceptions;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


@Slf4j
public class ExceptionUtils {
    //Stack Trace를 logger에 출력하기 위한 메서드
    static public String getStackTrace(Throwable throwable) {
        try (StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            log.error("stack trace를 문자열로 변환하는 과정에서 에러 발생함!");
            return null;
        }
    }
}
