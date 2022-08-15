package com.project.uandmeet.chat.dto;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {
    private String username;
    private String password;
    private String nickName;
    private String profileImgUrl;
//    private Long kakaoId;
//    private String naverId;
//    private int mannerTemp;
//    private Boolean isOwner = false; //관리자
    private String intro; //소개글
//    private int ParticipationCount;

    public MemberDto(Member member) {
        this.username = member.getUsername();
        this.password = member.getPassword();
        this.nickName = member.getNickname();
        this.profileImgUrl = member.getProfileImgUrl();
//        this.kakaoId = member.getKakaoId();
//        this.naverId = member.getNaverId();
//        this.mannerTemp = member.getMannerTemp();
//        this.isOwner = member.getIsOwner();
//        this.intro = member.getIntro();
//        this.bungCount = member.getBungCount();
    }
}
