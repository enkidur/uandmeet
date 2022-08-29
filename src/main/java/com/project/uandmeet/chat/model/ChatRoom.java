package com.project.uandmeet.chat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@Entity
public class ChatRoom {
    @Id
    private String chatRoomId;

//    @Column
//    private String lastMessage;
    @Column
    private Long lastMessageId;

    @OneToMany(mappedBy = "chatRoom")
    private List<JoinChatRoom> joinChatRoomList;

    // Profile_img와 유저아이디는 jwt토큰의 userdetails에서 확인이 가능
    // 또한 JoinChatRoom테이블에서 채팅방에 참가한 사람이 누구인지 조회가 가능
    // 마지막 전송시간 처리는 어떻게 해야할지 아직 모르겠음... ????___??????

    public ChatRoom() {
        this.chatRoomId = UUID.randomUUID().toString();
    }

    // 채팅방이 새로 만들어질 때 JoinChatRoom에도 추가가 된다.
    public void addJoinChatRooms(List<JoinChatRoom> joinChatRoomList) {
        this.joinChatRoomList = joinChatRoomList;
    }

    // lastMessage를 저장하는 메서드 추가
    public void addLastMessage(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}
