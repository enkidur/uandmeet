package com.project.uandmeet.chat.dto;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String nickName;
    private String profileUrl;
//    private Long kakaoId;
//    private String googleId;
//    private String naverId;
//    private int mannerTemp;
//    private Boolean isOwner = false;

    public UserDto(Member member) {
        this.username = member.getUsername();
        this.password = member.getPassword();
        this.nickName = member.getNickname();
        this.profileUrl = member.getProfileImgUrl();
//        this.kakaoId = member.getKakaoId();
//        this.googleId = member.getGoogleId();
//        this.naverId = member.getNaverId();
//        this.mannerTemp = member.getMannerTemp();
//        this.isOwner = member.getIsOwner();
    }
}
