package com.project.uandmeet.dto;

import com.project.uandmeet.model.Member;
import lombok.Data;

@Data
public class ReviewResponseDto {
    private String nickname;
    private String otherMember;

    public ReviewResponseDto(String nickname, String otherMember) {
        this.nickname = nickname;
        this.otherMember = otherMember;
    }
}
