package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MyPageInfoDto {
    private String username;
    private String gender;
    private Map<String, Long> birth;


    public MyPageInfoDto(String username, String gender, Map<String, Long> birth) {
        this.username = username;
        this.gender = gender;
        this.birth = birth;
    }
}
