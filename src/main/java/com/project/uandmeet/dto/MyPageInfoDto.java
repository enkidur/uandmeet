package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MyPageInfoDto {
    private String username;
    private boolean gender;
    private String birth;
    private String password;

    public MyPageInfoDto(String username, boolean gender, String birth) {
        this.username = username;
        this.gender = gender;
        this.birth = birth;
    }
}
