package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class ReviewStarRequestDto {
    private Long boardId;
    private Long toId;
    private Long score;
}
