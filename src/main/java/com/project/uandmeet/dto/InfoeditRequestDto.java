package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class InfoeditRequestDto {
    private boolean gender;
    private String birth;

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
