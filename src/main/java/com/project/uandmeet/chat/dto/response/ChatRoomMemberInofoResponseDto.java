package com.project.uandmeet.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChatRoomMemberInofoResponseDto {
    private String otherNickname;
    private String otherProfile;
    private Long memberId;
}
