package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.List;
@Getter
public class MyPostEntryResponseDto {
    private Long totalCount;
    private List<MyListEntryResponseDto> boardInfo;

    public MyPostEntryResponseDto(Long totalCount, List<MyListEntryResponseDto> boardInfo) {
        this.totalCount = totalCount;
        this.boardInfo = boardInfo;
    }
}
