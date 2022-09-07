package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MypostCommentResponseDto {
    private Long totalCount;
    private List<MyCommentResponseDto> commentInfo;

    public MypostCommentResponseDto(Long totalCount, List<MyCommentResponseDto> commentInfo) {
        this.totalCount = totalCount;
        this.commentInfo = commentInfo;
    }
}
