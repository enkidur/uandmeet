package com.project.uandmeet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginInfoDto {
    private Long id;
    private String nickname;
    private String email;
    private String profileImgUrl;

    public SocialLoginInfoDto(String nickname, String email, String profileImgUrl) {
        this.nickname = nickname;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
    }
}
