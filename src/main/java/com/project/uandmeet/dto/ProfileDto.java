package com.project.uandmeet.dto;

public class ProfileDto {
    private String nickname;
    private double star;
    private String profileimgurl;

    public ProfileDto(String nickname, double star, String profileimgurl) {
        this.nickname = nickname;
        this.star = star;
        this.profileimgurl = profileimgurl;
    }
}
