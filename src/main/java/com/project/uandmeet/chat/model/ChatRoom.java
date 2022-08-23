package com.project.uandmeet.chat.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatRoom extends Timestamped {
    @Id
    @Column(name = "chat_room_id")
    private Long id;

    @Column
    private String chatRoomName;

    @Column
    private Long boardId;

    @JsonManagedReference
    @OneToMany(mappedBy = "chatRoom",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<ChatRoomMember> member;


    public ChatRoom(String name){
        this.chatRoomName = name;
    }
}

