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
    private Long memberId;

    public UserinfoDto(String nickname, String profileImgUrl, Long memberId) {
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.memberId = memberId;
    }
}
