package com.milktea.main.util.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getFieldErrors();

        ErrorResponse errorResponse = new ErrorResponse(
                new ErrorResponse.Errors(
                        errors.stream()
                                .map(error -> String.format("%s : %s", error.getField(), error.getDefaultMessage()))
                                .toList()
                )

        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<?> handleValidationException(ValidationException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                new ErrorResponse.Errors(
                        List.of(String.format("Error Type : %s, Field: %s, Message: %s", e.getType(), e.getField(), e.getMessage()))
                )
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
