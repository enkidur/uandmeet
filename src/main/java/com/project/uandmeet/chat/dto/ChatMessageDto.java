package com.project.uandmeet.chat.dto;

import com.project.uandmeet.chat.model.ChatMessage;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long id;    //메시지 고유 id

    private ChatMessage.MessageType type;   // ENTER,TALK, QUIT 의 메시지 타입

    private String boardId; // 게시글 번호

    private String message; // 메시지

    private String createdAt;   //메시지 전송 시간

    private SenderDto member;   //보낸 회원
}