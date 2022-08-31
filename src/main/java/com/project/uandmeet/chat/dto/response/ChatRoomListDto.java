package com.project.uandmeet.chat.dto.response;

import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.model.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatRoomListDto {

    private Long id;
    private String chatRoomName;
//    private List<String> userList = new ArrayList<>();
    private List<Member> memberList = new ArrayList<>();
    private String nickname;

    private String roomCreator;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ChatRoomListDto(ChatRoom chatRoom, Member member) {
        this.id = chatRoom.getId();
        this.chatRoomName = chatRoom.getChatRoomName();
        this.memberList = chatRoom.getMemberList();
        this.nickname = member.getNickname();
        this.roomCreator = chatRoom.getRoomCreator();
        this.createdAt = chatRoom.getCreatedAt();
        this.modifiedAt = chatRoom.getModifiedAt();

    }
}
