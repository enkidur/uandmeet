package com.project.uandmeet.dto.response;

import com.project.uandmeet.model.Member;
import lombok.Getter;

@Getter
public class SignupResponseDto {
    private Long memberId;
    private String message;

    public SignupResponseDto(Member member, String message){
        this.memberId = member.getId();
        this.message = message;
    }
}
