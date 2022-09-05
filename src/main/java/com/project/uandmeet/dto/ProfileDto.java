package com.project.uandmeet.dto;

import com.project.uandmeet.model.Star;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileDto {
    private String nickname;
    private List<Star> star;
    private String profileimgurl;

    public ProfileDto(String nickname, List<Star> star, String profileimgurl) {
        this.nickname = nickname;
        this.star = star;
        this.profileimgurl = profileimgurl;
    }
    public ProfileDto(String nickname, List<Star> star) {
        this.nickname = nickname;
        this.star = star;
    }
}
