package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MyPageprofileDto {
    private String profileImgUrl;
    private String nickname;

    public MyPageprofileDto(String profileImgUrl, String nickname) {
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
    }
}
