package com.project.uandmeet.controller;

import com.project.uandmeet.dto.ReviewRequestDto;
import com.project.uandmeet.dto.ReviewStarRequestDto;
import com.project.uandmeet.dto.ReviewResponseDto;
import com.project.uandmeet.model.Review;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // review 작성 페이지
    @GetMapping("/api/reviewstar")
    public ResponseEntity<ReviewResponseDto> Review(@AuthenticationPrincipal UserDetailsImpl userDetails, String otherNcik) {
        String nickname = userDetails.getMember().getNickname();
        return ResponseEntity.ok(reviewService.review(nickname, otherNcik));
    }

    // review 별점 작성
    @PostMapping("/api/reviewstar")
    public ResponseEntity<Review> insertScore(@AuthenticationPrincipal UserDetailsImpl userDetails, ReviewStarRequestDto requestDto) {

        return ResponseEntity.ok(reviewService.insertScore(userDetails, requestDto));
    }

    @PostMapping("/api/reivew")
    public ResponseEntity<Review> createReview(@AuthenticationPrincipal UserDetailsImpl userDetails, ReviewRequestDto requestDto) {
        return ResponseEntity.ok(reviewService.createReview(userDetails, requestDto));
    }
}
