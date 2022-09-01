package com.project.uandmeet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.project.uandmeet.exception.*;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { CustomException.class })
    public ResponseEntity<com.project.uandmeet.exception.ErrorResponse> handleApiRequestException(CustomException ex) {
        return com.project.uandmeet.exception.ErrorResponse.toResponseEntity(ex.getErrorCode());
    }
}