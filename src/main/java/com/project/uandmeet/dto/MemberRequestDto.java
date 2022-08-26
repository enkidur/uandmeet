package com.project.uandmeet.dto;

import com.project.uandmeet.model.Image;
import com.project.uandmeet.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class MemberRequestDto {
    private Long memberId;
    private String username;

    private String nickname;

    public static MemberRequestDto fromMember(Member member){

        MemberRequestDto requestDto = new MemberRequestDto();

        requestDto.memberId = member.getId();
        requestDto.username = member.getUsername();
        requestDto.nickname = member.getNickname();
    return null;
    }
}
