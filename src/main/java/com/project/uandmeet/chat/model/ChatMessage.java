package com.project.uandmeet.chat.model;

import com.project.uandmeet.model.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatMessage {

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //메시지 고유 id

    @Column
    private String boardId; // 게시글 아이디값

//    @Enumerated(EnumType.STRING)
    @Column
    private MessageType type; // 메시지 타입

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 메시지 보낸사람

    @Column
    private String message; // 메시지

    @Column
    private String createdAt;   //전송시간


}
