package com.project.uandmeet.chat.model;

import com.project.uandmeet.chat.dto.request.MessageRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter @Setter
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    private ChatRoom chatroom; // 방번호

    @Column
    private MessageType messageType;

    @Column
    private String message; // 메시지

    @Column
    private Long senderId;

    @Column
    private String createdAt;


    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Builder
    public ChatMessage(ChatRoom chatRoom, Long senderId, MessageRequestDto message, String createdAt){
        this.senderId = senderId;
        this.chatroom = chatRoom;
        this.message = message.getMessage();
        this.messageType = message.getType();
        this.createdAt = createdAt;
    }
}