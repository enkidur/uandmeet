package com.project.uandmeet.chat.model;

import com.project.uandmeet.chat.dto.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class ChatMessage {
    // 메시지 타입 : 입장, 채팅, 나가기
    public enum MessageType {
        ENTER, TALK, QUIT
    }
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String roomId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;
    @Column(nullable = false)
    private String sender;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private String profileImgUrl;
    @Column
    private Long enterMemberCnt;
    @Column(nullable = false)
    private Long memberId ;
    @Column(nullable = false)
    private LocalDateTime createdAt;
//    @Column
//    private String fileUrl;
    @Column
    private Boolean quitOwner = false;


    public ChatMessage(ChatMessageDto chatMessageDto, LocalDateTime createdAt) {
        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.message = chatMessageDto.getMessage();
        this.sender = chatMessageDto.getSender();
        this.profileImgUrl = chatMessageDto.getProfileImgUrl();
        this.enterMemberCnt = chatMessageDto.getEnterMemberCnt();
        this.memberId = chatMessageDto.getMemberId();
        this.createdAt = createdAt;
//        this.fileUrl = chatMessageDto.getFileUrl();
    }
}
