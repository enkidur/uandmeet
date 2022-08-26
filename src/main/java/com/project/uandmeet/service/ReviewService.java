package com.project.uandmeet.service;


import com.project.uandmeet.dto.ReviewRequestDto;
import com.project.uandmeet.dto.ReviewStarRequestDto;
import com.project.uandmeet.dto.ReviewResponseDto;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.Review;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.repository.ReviewRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepostiory;
    private final BoardRepository boardRepository;

    public ReviewResponseDto review(String nickname, String otherNick) {
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(nickname, otherNick);
        return reviewResponseDto;
    }

    public Review insertScore(UserDetailsImpl userDetails, ReviewStarRequestDto requestDto) {
        Member from = userDetails.getMember();
        Member to = memberRepostiory.findById(requestDto.getToId()).orElseThrow(
                ()->new RuntimeException("존재하지 않는 사용자입니다.")
        );
        Board board = boardRepository.findById(requestDto.getBoardId()).orElseThrow(
                () -> new RuntimeException("Invalid board id")
        );
        Review review = new Review(board, from, to, requestDto.getScore());
        reviewRepository.save(review);
        return review;
    }

    public Review createReview(UserDetailsImpl userDetails, ReviewRequestDto requestDto) {
        Member from = userDetails.getMember();
        Member to = memberRepostiory.findById(requestDto.getToId()).orElseThrow(
                ()->new RuntimeException("존재하지 않는 사용자입니다.")
        );
        Board board = boardRepository.findById(requestDto.getBoardId()).orElseThrow(
                () -> new RuntimeException("Invalid board id")
        );
        Review review = new Review(board, from, to, requestDto.getNum(), requestDto.getReview());
        return review;
    }
}
