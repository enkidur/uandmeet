package com.project.uandmeet.chat.dto.request;


import com.project.uandmeet.chat.model.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestDto {
    private String chatRoomId;
    private ChatMessage.MessageType type;
    private String message;
    private String createdAt;
}
