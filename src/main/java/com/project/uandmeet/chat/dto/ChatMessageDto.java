package com.project.uandmeet.chat.dto;

import com.project.uandmeet.chat.model.ChatMessage;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long id;

    private ChatMessage.MessageType type;

    private String boardId; // 게시글 번호

    private String message; // 메시지

    private String createdAt;

    private SenderDto member;
}