package com.project.uandmeet.dto.request;

import lombok.Getter;

@Getter
public class FindPasswordRequestDto {
    private String username;
    private String password;
    private String passwordCheck;
}
