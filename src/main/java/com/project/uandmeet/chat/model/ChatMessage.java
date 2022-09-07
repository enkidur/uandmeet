package com.project.uandmeet.chat.model;

import com.project.uandmeet.chat.dto.ChatMessageRequestDto;
import com.project.uandmeet.service.MemberService;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //메시지 고유 id

    @Column
    private String roomId;  //채팅방 번호

    @Enumerated(EnumType.STRING)
    @Column
    private MessageType type; // 메시지 타입

    @Column
    private String sender; // 메시지 보낸사람

    @Column
    private String message; // 메시지

    @Column
    private long memberCount;   //채팅방 인원수

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String message, long memberCount) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.memberCount = memberCount;
    }

    @Builder
    public ChatMessage(ChatMessageRequestDto chatMessageRequestDto, MemberService memberService) {
        this.type = chatMessageRequestDto.getType();
        this.roomId = chatMessageRequestDto.getRoomId();
        this.sender =  memberService.getMember(chatMessageRequestDto.getSender()).getNickname();
        this.message = chatMessageRequestDto.getMessage();
        this.memberCount = chatMessageRequestDto.getMemberCount();
    }
}
