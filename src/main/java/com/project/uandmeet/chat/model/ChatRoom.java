package com.project.uandmeet.chat.model;



import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom extends Timestamped implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="RECEIVERID")
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SENDERID")
    private Member sender;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    public static ChatRoom create(ChatRoom create){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.receiver = create.getReceiver();
        chatRoom.sender = create.getSender();
        return chatRoom;
    }

}
