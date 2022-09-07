package com.project.uandmeet.chat.dto;

import com.project.uandmeet.chat.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private long memberCount;
}
