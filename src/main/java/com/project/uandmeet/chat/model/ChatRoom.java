package com.project.uandmeet.chat.model;

import com.project.uandmeet.chat.dto.MemberDto;
import com.project.uandmeet.model.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(nullable = false)
    private String roomId;
    @Column(nullable = false)
    private String username;

    //채팅방 생성
    public static ChatRoom create(Board board, MemberDto memberDto) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(board.getId());
        chatRoom.username= memberDto.getUsername();
        return chatRoom;
    }
}
