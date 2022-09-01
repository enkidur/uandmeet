package com.project.uandmeet.chat.dto;

import com.project.uandmeet.chat.model.ChatMessage;

public interface FindChatMessageDto {

    Long getId();

    ChatMessage.MessageType getType();

    String getBoardId();

    String getMessage();

    String getCreatedAt();

    member getMember();

    interface member{
        Long getId();
        String getProfile();
//        String getRole();
        String getNickname();
    }
}