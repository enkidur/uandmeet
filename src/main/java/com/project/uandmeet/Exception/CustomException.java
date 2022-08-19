package com.project.uandmeet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class CustomException extends RuntimeException{
    private final com.project.uandmeet.exception.ErrorCode errorCode;
}
