package com.project.uandmeet.dto;

import lombok.Getter;


@Getter
public class ProfileDto {
    private String nickname;
    private Double star;
    private String profileimgurl;

    public ProfileDto(String nickname, Double star, String profileimgurl) {
        this.nickname = nickname;
        this.star = star;
        this.profileimgurl = profileimgurl;
    }
    public ProfileDto(String nickname, Double star) {
        this.nickname = nickname;
        this.star = star;
    }
}
