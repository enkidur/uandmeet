package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MyCommentResponseDto {
    private Long id;

    //글내용
    private String comment;

    //테이블타입 정보공유 : information,  매칭:matching
    private String boardType;
    MyListMemberResponseDto writer;

    public MyCommentResponseDto(Long id, String comment, String boardType, MyListMemberResponseDto writer) {
        this.id = id;
        this.comment = comment;
        this.boardType = boardType;
        this.writer = writer;
    }
}
