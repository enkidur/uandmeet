package com.project.uandmeet.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ChatRoomResponseDto {
    private String lastMessage;
    private String boardUrl;
    private String boardTitle;
    private String lastMessageTime;
    private boolean isLetter;
    private String boardTime;
    private Long boardId;
}
