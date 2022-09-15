package com.project.uandmeet.service;

import com.project.uandmeet.dto.*;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.Review;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.repository.EntryRepository;
import com.project.uandmeet.repository.ReviewRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BoardRepository boardRepository;
    private final EntryRepository entryRepository;

    public ReviewResponseDto review(UserDetailsImpl userDetails, Long boardId) {
        String nickname = userDetails.getMember().getNickname();

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );

        String otherMember = board.getMember().getNickname();

        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(nickname, otherMember);
        return reviewResponseDto;
    }


    public ReviewDto createReview(UserDetailsImpl userDetails, ReviewRequestDto requestDto) throws ParseException {
        Member from = userDetails.getMember();
        Board board = boardRepository.findByBoardTypeAndId("matching", requestDto.getBoardId()).orElseThrow(
                () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 형식 통일
        Date date = sdf.parse(board.getEndDateAt());//String-->Date
        Long dateLong = date.getTime();//Date-->Long
        long now = Timestamp.valueOf(LocalDateTime.now()).getTime(); // 현재

        // 해당 매칭에 참여하지 않았거나 매칭 만료일이 지나지 않을 시
        if (!(entryRepository.existsByMemberAndAndBoard(from, board)) || (dateLong > now)) {
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
        }

        // 이미 참여한 리뷰일 경우
        if (reviewRepository.existsByFromAndBoardId(from.getId(), board.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        List<Integer> nums = new ArrayList<>(); // 초기화
        for (int num : requestDto.getNum()) {
            nums.add(num);
        }

        Review review = new Review(board.getId(), from.getId(), board.getMember().getId(), nums, requestDto.getScore(), requestDto.getReview());
        reviewRepository.save(review);
        return new ReviewDto(board.getId(),
                from.getId(),
                board.getMember().getId(),
                board.getMember().getNickname(),
                nums,
                requestDto.getScore(),
                requestDto.getReview());
    }
}
