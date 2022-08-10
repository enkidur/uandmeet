package com.project.uandmeet.chat.model;


import com.project.uandmeet.model.Timestamped;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITERID")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHATROOMID")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String message;

    @Column
    private String messageModifiedDate;

    @Column
    private String messageModifiedTime;

}
