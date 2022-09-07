package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MyListMemberResponseDto {
    private String username;
    private String nickname;
    private String profile; // 이미지

    public MyListMemberResponseDto(String username, String nickname, String profile) {
        this.username = username;
        this.nickname = nickname;
        this.profile = profile;
    }
}
