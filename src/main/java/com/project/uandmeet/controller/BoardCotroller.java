package com.project.uandmeet.controller;

import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardResponseDto;
import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
import com.project.uandmeet.dto.commentsDtoGroup.CommentsInquiryDto;
import com.project.uandmeet.dto.commentsDtoGroup.CommentsReponseDto;
import com.project.uandmeet.dto.commentsDtoGroup.CommentsRequestDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardCotroller {
    private final BoardService boardService;

    //게시물 작성
    @PostMapping("/api/board/create")
    private CustomException boardNew(@RequestBody BoardRequestDto.createAndCheck boardRequestDto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.boardNew(boardRequestDto, userDetails);
    }

    //게시물 전체 조회
    @GetMapping("/api/boards/{board_type}/{category_name}")
    private List<BoardResponseDto> boardAllInquiry(@PathVariable("board_type") String boardType,
                                                   @PathVariable("category_name") String categoryName) {
        List<BoardResponseDto> boardAllInquiry = boardService.boardAllInquiry(boardType, categoryName);

        if (boardAllInquiry.size() == 0) {
            throw new CustomException(ErrorCode.CAN_NOT_CREATE_ROOM);
        } else
            return boardAllInquiry;
    }

    //게시물 상세 조회
    @GetMapping("/api/boards/{id}")
    private BoardResponseDto boardChoiceInquiry(@PathVariable("id") Long id) {
        BoardResponseDto boardChoiceInquiry = null;
        boardChoiceInquiry = boardService.boardChoiceInquiry(id);
        if (boardChoiceInquiry == null) {
            throw new CustomException(ErrorCode.CAN_NOT_CREATE_ROOM);
        } else
            return boardChoiceInquiry;

    }

    //개시물 수정
    @PutMapping("/api/board/{id}")
    private CustomException boardUpdate(@PathVariable("id") Long id,
                                        @RequestBody BoardRequestDto.createAndCheck boardRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.boardUpdate(id, boardRequestDto, userDetails);
    }

    //게시물 삭제.
    @DeleteMapping("/api/board/{id}")
    private CustomException boardDel(@PathVariable("id") Long id,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return boardService.boardDel(id, userDetails);
    }

    //좋아요 유무
    @PostMapping("/board/likes")
    private ResponseEntity<Long> likeClick(LikeDto likeDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.likeClick(likeDto, userDetails);

    }

    //매칭참여
    @PostMapping("/board/{id}/matchingentry")
    private ResponseEntity<Long> matchingJoin(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.matchingJoin(id, userDetails);

    }

    //매칭취소
    @PostMapping("/board/{id}/matchingentrycancel")
    private ResponseEntity<Long> matchingCancel(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.matchingCancel(id, userDetails);

    }
    
    //댓글작성
    @PostMapping("/api/board/{id}/comments")
    private CommentsReponseDto commentsNew(@PathVariable("id") Long id,
                                           CommentsRequestDto commentsRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.commentsNew(id,commentsRequestDto,userDetails);
    }

    //댓글 전체 조회
    @GetMapping("/api/board/{id}/comments")
    private List<CommentsInquiryDto> commentInquiry(@PathVariable("id") Long id) {
        return boardService.commentInquiry(id);
    }

    //댓글 삭제
    @DeleteMapping("/api/board/{boardId}/comments/{commentId}")
    private CustomException commentDel(@PathVariable("boardId") Long boardId,
                                       @PathVariable("commentId") Long commentId,
                            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        return boardService.commentDel(boardId,commentId, userDetails);

    }
}
