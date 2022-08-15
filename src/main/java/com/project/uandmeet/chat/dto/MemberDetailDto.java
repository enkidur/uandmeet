package com.project.uandmeet.chat.dto;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDetailDto {
    private String nickname;
    private String profileImgUrl;

//    private int ParticipationCount; //참여횟수
//    private int mannerTemp; //매너온도
//    private String intro; //소개글
    private Boolean response;
    private String message;
    private Boolean chatOwner; //채팅방 방장
    public MemberDetailDto(Boolean response, String message, Member member, Boolean chatOwner) {
        this.response = response;
        this.message = message;
        this.nickname = member.getNickname();
        this.profileImgUrl = member.getProfileImgUrl();
//        this.bungCount = member.getBungCount();
//        this.mannerTemp = member.getMannerTemp();
//        this.intro = member.getIntro();
        this.chatOwner =chatOwner;
    }
}
