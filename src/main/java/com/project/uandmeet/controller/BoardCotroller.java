package com.project.uandmeet.controller;

import com.project.uandmeet.Exception.CustomException;
import com.project.uandmeet.Exception.ErrorCode;
import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardResponseDto;
import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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
    private BoardResponseDto boardChoiceInquiry(@PathVariable("id") Long id)
    {   BoardResponseDto boardChoiceInquiry = null;
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
    @DeleteMapping ("/api/board/{id}")
    private CustomException boardDel(@PathVariable("id") Long id,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails){

        return  boardService.boardDel(id,userDetails);
    }

    //좋아요 유무
    @PostMapping("/board/likes")
    private CustomException likeClick(LikeDto likeDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.likeClick(likeDto,userDetails);

    }
}