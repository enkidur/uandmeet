package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MainPageDto {
    private Long boardId;
    private String title;
    private String content;
    private String nickname;
    private String boardimage;

    public MainPageDto(Long boardId, String title, String content, String nickname, String boardimage) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.boardimage = boardimage;
    }
}
