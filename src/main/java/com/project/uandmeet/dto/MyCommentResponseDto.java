package com.project.uandmeet.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyCommentResponseDto {
    private Long id;
    private Long boardId;
    private LocalDateTime createdAt;

    //글내용
    private String comment;

    //테이블타입 정보공유 : information,  매칭:matching
    private String boardType;
    MyListMemberResponseDto writer;

    public MyCommentResponseDto(Long id, Long boardId, LocalDateTime createdAt ,String comment, String boardType, MyListMemberResponseDto writer) {
        this.id = id;
        this.boardId = boardId;
        this.createdAt = createdAt;
        this.comment = comment;
        this.boardType = boardType;
        this.writer = writer;
    }
}
