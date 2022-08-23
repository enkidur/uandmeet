package com.project.uandmeet.chat.dto;

import com.project.uandmeet.model.Member;
import lombok.Getter;

@Getter
public class NicknameResponseDto {

    private Long memberId;

    private String nickname;

    private String username;

    public NicknameResponseDto(Member member){
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.username = member.getUsername();

    }
}
