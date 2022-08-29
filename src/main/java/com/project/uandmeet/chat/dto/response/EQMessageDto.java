package com.project.uandmeet.chat.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EQMessageDto {
    private String eqMessage;
    private String chatroomId;
    public EQMessageDto(String eqMessage, String chatroomId){
        this.eqMessage=eqMessage;
        this.chatroomId=chatroomId;
    }
}

