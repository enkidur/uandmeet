package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SimpleReviewResponseDto {
    Map<Integer, Integer> review;
//    Map<Integer, Long> plusReview;
//    Map<Integer, Long> minusReview;
    public SimpleReviewResponseDto(Map<Integer, Integer> review) {
        this.review = review;
    }

//    public SimpleReviewResponseDto(Map<Integer, Long> plusReview, Map<Integer, Long> minusReview) {
//        this.plusReview = plusReview;
//        this.minusReview = minusReview;
//    }
}
