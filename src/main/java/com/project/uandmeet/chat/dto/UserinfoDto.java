package com.project.uandmeet.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserinfoDto {
    private String nickname;
    private String profileImgUrl;
    private Long userId;

    public UserinfoDto(String nickname, String profileImgUrl, Long userId) {
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.userId = userId;
    }
}
