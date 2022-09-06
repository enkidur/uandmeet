package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class SimpleReviewResponseDto {
    Map<Integer, Long> review;
//    Map<Integer, Long> minusReview;

    public SimpleReviewResponseDto(Map<Integer, Long> review) {
        this.review = review;
    }
}
