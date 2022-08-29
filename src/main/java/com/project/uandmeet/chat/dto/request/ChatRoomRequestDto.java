package com.project.uandmeet.chat.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatRoomRequestDto {

    private String chatRoomName;
    private String nickname;

}
