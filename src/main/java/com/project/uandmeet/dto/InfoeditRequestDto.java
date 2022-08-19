package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class InfoeditRequestDto {
    private String gender;
    private String birth;

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
