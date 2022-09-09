package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MainPageDto {
    private Long boardId;
    private String title;
    private String content;
    private String nickname;

    public MainPageDto(Long boardId, String title, String content, String nickname) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
    }
}
