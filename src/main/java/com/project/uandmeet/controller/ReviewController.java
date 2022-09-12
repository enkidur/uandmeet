package com.project.uandmeet.controller;

import com.project.uandmeet.dto.*;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.Review;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // review 작성 페이지
    @GetMapping("/api/review")
    public ResponseEntity<ReviewResponseDto> Review(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestBody BoardIdRequestDto requestDto) {

        return ResponseEntity.ok(reviewService.review(userDetails, requestDto));
    }

    //review 작성
    @PostMapping("/api/review")
    public ResponseEntity<ReviewDto> createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody ReviewRequestDto requestDto) throws ParseException {
        return ResponseEntity.ok(reviewService.createReview(userDetails, requestDto));
    }
}
