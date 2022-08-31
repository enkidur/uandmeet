package com.project.uandmeet.chat.dto.response;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Setter
@Getter
public class ChatRoomUserResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String profileUrl;

    public ChatRoomUserResponseDto(Member member){
        this.id = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.profileUrl = member.getProfile();
    }
}
