package com.project.uandmeet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { com.project.uandmeet.exception.CustomException.class })
    public ResponseEntity<ErrorResponse> handleApiRequestException(com.project.uandmeet.exception.CustomException ex) {
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }
}