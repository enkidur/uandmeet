package com.project.uandmeet.chat.dto.response;

import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ChatRoomResponseDto {

    private Long id;
    private String chatRoomName;
    private String nickname;

    public ChatRoomResponseDto(ChatRoom chatRoom, Member member) {
        this.id = chatRoom.getId();
        this.chatRoomName = chatRoom.getChatRoomName();
        this.nickname = member.getNickname();
    }
}
