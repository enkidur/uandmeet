package com.project.uandmeet.dto;

import com.project.uandmeet.model.Board;
import lombok.Getter;

import javax.persistence.Column;
import java.util.List;

@Getter
public class MypostResponseDto {
    private Long totalCount;
//    private List<Board> boardInfo;
    private List<MyListResponseDto> boardInfo;

//    public MypostResponseDto(Long totalCount, List<Board> boardInfo) {
//        this.totalCount = totalCount;
//        this.boardInfo = boardInfo;
//    }


    public MypostResponseDto(Long totalCount, List<MyListResponseDto> boardInfo) {
        this.totalCount = totalCount;
        this.boardInfo = boardInfo;
    }
}
