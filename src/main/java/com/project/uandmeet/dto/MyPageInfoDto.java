package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyPageInfoDto {
    private String username;
    private String gender;
    private List<Long> birth;
    private String password;

    public MyPageInfoDto(String username, String gender, List<Long> birth) {
        this.username = username;
        this.gender = gender;
        this.birth = birth;
    }
}
