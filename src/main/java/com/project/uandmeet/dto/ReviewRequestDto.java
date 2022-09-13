package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class ReviewRequestDto {
    private int[] num;
    private String review;
    private Long boardId;
    private Long score;
}
