package com.milktea.main.util.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


//당장은 사용하지 않고 잠시 보류
@Getter
@AllArgsConstructor
public class ValidationException extends RuntimeException {
    private final ErrorType type;
    private final String field;
    private final String message;
    private final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    public enum ErrorType {
            INVALID_EMAIL,
            DUPLICATE_USERNAME,
            INVALID_PASSWORD
    }
}
