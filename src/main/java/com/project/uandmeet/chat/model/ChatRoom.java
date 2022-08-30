package com.project.uandmeet.chat.model;

import com.project.uandmeet.chat.dto.request.ChatRoomRequestDto;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.service.MemberService;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class ChatRoom extends Timestamped {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column
    private String chatRoomName;

    @Column
    private Long boardId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private List<Member> memberList = new ArrayList<>();

    private String roomCreator;


    public ChatRoom(ChatRoomRequestDto requestDto, MemberService memberService) {
        this.chatRoomName = requestDto.getChatRoomName();
        this.memberList.add(memberService.findByNickname(requestDto.getNickname()));
        this.roomCreator = requestDto.getNickname();
    }

    public ChatRoom(String name){
        this.chatRoomName = name;
    }
}

