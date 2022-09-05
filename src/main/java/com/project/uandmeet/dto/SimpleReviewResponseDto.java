package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class SimpleReviewResponseDto {
    Map<Integer, Long> plusReview;
    Map<Integer, Long> minusReview;

    public SimpleReviewResponseDto(Map<Integer, Long> plusReview, Map<Integer, Long> minusReview) {
        this.plusReview = plusReview;
        this.minusReview = minusReview;
    }
}
