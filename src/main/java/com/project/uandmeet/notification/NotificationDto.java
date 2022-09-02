package com.project.uandmeet.notification;

import lombok.*;

public class NotificationDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{

        private Long id;
        private String content;
        private Boolean isRead;
    }
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Publish{

        private Long id;
        private String content;
        private Boolean isRead;
        private Long reciverId;
    }
}
