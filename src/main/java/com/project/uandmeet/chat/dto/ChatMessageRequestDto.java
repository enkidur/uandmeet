package com.project.uandmeet.chat.dto;

import lombok.*;

public class ChatMessageRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Write{
        private Long memberId;
        private Long receiverId;
        private Long chatRoomId;
        private String modifiedAt;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WriteSubscriber{

        private Long memberId;
        private String memberNickname;
        private String messageModifiedDate;
        private String messageModifiedTime;
        private Long chatRoomId;
        private String message;
    }
}
