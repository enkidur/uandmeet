package com.project.uandmeet.chat.model;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class JoinChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean isJoinChatRoomOut;

    @ManyToOne
    private Member member;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private ChatRoom chatRoom;

    public JoinChatRoom(Member member, ChatRoom chatRoom, boolean isJoinChatRoomOut) {
        this.member = member;
        this.chatRoom = chatRoom;
        this.isJoinChatRoomOut = isJoinChatRoomOut;
    }

    public void isOut(boolean isJoinChatRoomOut) {
        this.isJoinChatRoomOut = isJoinChatRoomOut;
    }
}
