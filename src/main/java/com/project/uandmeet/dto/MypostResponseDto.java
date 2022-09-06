package com.project.uandmeet.dto;

import com.project.uandmeet.model.Board;
import lombok.Getter;

import java.util.List;

@Getter
public class MypostResponseDto {
    private Long totalCount;
    private List<Board> boardInfo;

    public MypostResponseDto(Long totalCount, List<Board> boardInfo) {
        this.totalCount = totalCount;
        this.boardInfo = boardInfo;
    }
}
