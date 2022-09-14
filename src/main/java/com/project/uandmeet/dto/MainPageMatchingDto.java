package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MainPageMatchingDto {
    private String category;
    private Long boardId;
    private String title;
    private String content;
    private String nickname;
    private String endDateAt;
    private Long currentEntry;
    private Long maxEntry;
    private String boardimage;
    private Long likeCount;

    public MainPageMatchingDto(String category, Long boardId, String title, String content, String nickname, String endDateAt, Long currentEntry, Long maxEntry, String boardimage, Long likeCount) {
        this.category = category;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.endDateAt = endDateAt;
        this.currentEntry = currentEntry;
        this.maxEntry = maxEntry;
        this.boardimage = boardimage;
        this.likeCount = likeCount;
    }
}
