package com.project.uandmeet.chat.dto;

import com.project.uandmeet.chat.model.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequestDto {

    private ChatMessage.MessageType type;
    private String roomId;
    private String nickname;
    private String sender;
    private String message;
}
