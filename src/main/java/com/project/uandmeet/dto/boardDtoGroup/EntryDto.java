package com.project.uandmeet.dto.boardDtoGroup;

import lombok.*;



public class EntryDto {
    public static response response;

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class request {
        private Long boardId;
        private Boolean isMatching;
    }

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class response {
        private Long currentEntry;
    }
}