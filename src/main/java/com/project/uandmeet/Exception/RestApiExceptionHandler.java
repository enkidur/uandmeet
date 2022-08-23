package com.project.uandmeet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { CustomException.class })
    public ResponseEntity<com.project.uandmeet.Exception.ErrorResponse> handleApiRequestException(CustomException ex) {
        return com.project.uandmeet.Exception.ErrorResponse.toResponseEntity(ex.getErrorCode());
    }
}