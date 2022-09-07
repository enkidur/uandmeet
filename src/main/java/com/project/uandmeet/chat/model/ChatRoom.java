package com.project.uandmeet.chat.model;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @Column
    private String roomId;

    @Column
    private long memberCount;

    @OneToOne(mappedBy = "chatRoom")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNMEMBER_ID")
    private Member ownMember;

    public ChatRoom(Member ownMember, Board board){
        this.ownMember = ownMember;
        this.board = board;
        this.roomId = UUID.randomUUID().toString();
    }

    public ChatRoom(String uuid){
        this.roomId = uuid;
    }
}
