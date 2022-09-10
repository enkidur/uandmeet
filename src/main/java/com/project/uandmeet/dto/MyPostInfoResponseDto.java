package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyPostInfoResponseDto {
    private Long totalCount;
    //    private List<Board> boardInfo;
    private List<MyListInfoResponseDto> boardInfo;

    public MyPostInfoResponseDto(Long totalCount, List<MyListInfoResponseDto> boardInfo) {
        this.totalCount = totalCount;
        this.boardInfo = boardInfo;
    }
}