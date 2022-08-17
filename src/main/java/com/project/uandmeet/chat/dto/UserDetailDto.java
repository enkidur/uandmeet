package com.project.uandmeet.chat.dto;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailDto {
    private String nickname;
    private String profileUrl;
//    private int mannerTemp;
//    private String intro;
    private Boolean response;
    private String message;

    private Boolean chatOwner;
    public UserDetailDto(Boolean response, String message, Member member, Boolean chatOwner) {
        this.response = response;
        this.message = message;
        this.nickname = member.getNickname();
        this.profileUrl = member.getProfileImgUrl();
//        this.mannerTemp = member.getMannerTemp();
//        this.intro = member.getIntro();
        this.chatOwner =chatOwner;
    }
}
