package com.project.uandmeet.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageResponseDto {
    private String message;
    private String createdAt;
    private Long memberId;
    private String imageUrl;
    private String chatroomId;

    @Builder
    public MessageResponseDto(String message, String createdAt,Long memberId,String imageUrl) {
//        this.nickname = nickname;
        this.message = message;
        this.createdAt = createdAt;
        this.memberId=memberId;
        this.imageUrl=imageUrl;
    }

    public MessageResponseDto(String message, String createdAt,Long memberId,String imageUrl,String chatroomId) {
//        this.nickname = nickname;
        this.message = message;
        this.createdAt = createdAt;
        this.memberId=memberId;
        this.imageUrl=imageUrl;
        this.chatroomId=chatroomId;
    }
}
