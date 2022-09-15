package com.project.uandmeet.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class ReviewRequestDto {
    private int[] num;
    private String review;
    private Long boardId;
    private Long score;
}
