package com.project.uandmeet.controller;

import com.project.uandmeet.dto.SearchResponseDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardResponseDto;
import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.BoardService;
import com.project.uandmeet.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardCotroller {
    private final BoardService boardService;

    private final SearchService searchService;

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


    // 검색
    @GetMapping("/board/search")
    public ResponseEntity<List<SearchResponseDto>> search(@RequestParam(value = "sort") String sort,
                                                          @RequestParam(value = "keyword") String keyword,
                                                          @RequestParam(value = "city") String city,
                                                          @RequestParam(value = "gu") String gu) {

        List<SearchResponseDto> searchResponseDto = searchService.queryDslSearch(sort, keyword, city, gu);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(searchResponseDto);

    }

/*
    @PostMapping("/board/{id}/matchingentry")
    private ResponseEntity<Long> matchingJoin(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.matchingJoin(id,userDetails);

    }*/
}