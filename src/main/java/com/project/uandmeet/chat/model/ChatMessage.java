package com.project.uandmeet.chat.model;

import com.project.uandmeet.chat.dto.request.ChatMessageRequestDto;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.service.MemberService;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private MessageType type;

    @Column
    private String roomId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id_joined")
    private Member member;

    // Redis MessageListener 로 뒙소켓을 통해 바로 채팅방에 메시지를 전달해주기 위한 값을 따로 설정해주었다
    @Column
    private String nickname;

    @Column
    private String sender;

    @Column
    private String message;

    @Column
    private String createdAt;


    @Builder
    public ChatMessage(ChatMessageRequestDto chatMessageRequestDto, MemberService memberService) {
        this.type = chatMessageRequestDto.getType();
        this.roomId = chatMessageRequestDto.getRoomId();
        this.member = memberService.findByNickname(chatMessageRequestDto.getNickname());
        this.nickname = chatMessageRequestDto.getNickname();
        this.sender = chatMessageRequestDto.getSender();
        this.message = chatMessageRequestDto.getMessage();
        this.createdAt = createdAt;
    }
}
