package com.project.uandmeet.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomResponseDto {
    private String chatRoomId;
    private String senderNickname;
    private Long otherId;
    private String image;
    private String createdAt;
    private String lastMessage;
}
