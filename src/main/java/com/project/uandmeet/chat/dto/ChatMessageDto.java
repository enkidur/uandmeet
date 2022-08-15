package com.project.uandmeet.chat.dto;

import com.project.uandmeet.chat.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private ChatMessage.MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String message; // 메시지
    private String sender; // 닉네임
    private String profileImgUrl; //프로필이미지
    private Long enterMemberCnt; //몇명
    private Long memberId; //회원 고유아이디
    private String createdAt; //채팅 전송시간
    private Boolean quitOwner;


    public ChatMessageDto(ChatMessage chatMessage, String createdAt) {
        this.type = chatMessage.getType();
        this.roomId = chatMessage.getRoomId();
        this.message = chatMessage.getMessage();
        this.sender = chatMessage.getSender();
        this.profileImgUrl = chatMessage.getProfileImgUrl();
        this.enterMemberCnt = chatMessage.getEnterMemberCnt();
        this.memberId = chatMessage.getMemberId();
        this.createdAt = createdAt;
        this.quitOwner = chatMessage.getQuitOwner();
    }
}