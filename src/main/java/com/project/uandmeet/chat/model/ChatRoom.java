package com.project.uandmeet.chat.model;


import com.project.uandmeet.chat.dto.UserDto;
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
    public static ChatRoom create(Board board, UserDto userDto) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(board.getBoardId());
        chatRoom.username=userDto.getUsername();
        return chatRoom;
    }
}
