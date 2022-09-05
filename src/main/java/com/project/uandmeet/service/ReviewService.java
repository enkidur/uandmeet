package com.project.uandmeet.service;


import com.project.uandmeet.dto.*;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepostiory;
    private final BoardRepository boardRepository;

    @Transactional
    public ReviewResponseDto review(UserDetailsImpl userDetails, BoardIdRequestDto requestDto) {
        String nickname = userDetails.getMember().getNickname();
        Board board = boardRepository.findById(requestDto.getBoardId()).orElseThrow(
                ()-> new RuntimeException("찾을 수 없는 게시글입니다.")
        );
        Member otherMember = board.getMember();
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(nickname, otherMember);
        return reviewResponseDto;
    }


    @Transactional
    public ReviewDto createReview(UserDetailsImpl userDetails, ReviewRequestDto requestDto) {
        Member from = userDetails.getMember();
//        Member to = memberRepostiory.findById(requestDto.getToId()).orElseThrow(
//                ()->new RuntimeException("존재하지 않는 사용자입니다.")
//        );
        Board board = boardRepository.findById(requestDto.getBoardId()).orElseThrow(
                () -> new RuntimeException("찾을 수 없는 게시글입니다.")
        );
        Review review = new Review(board, from, board.getMember(), requestDto.getNum(), requestDto.getScore() ,requestDto.getReview());
        reviewRepository.save(review);
        return new ReviewDto(board.getId(),
                from.getId(),
                board.getMember().getId(),
                board.getMember().getNickname(),
                board.getMember().getProfile(),
                requestDto.getNum(),
                requestDto.getScore(),
                requestDto.getReview());
    }
}
