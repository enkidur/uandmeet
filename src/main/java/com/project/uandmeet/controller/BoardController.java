package com.project.uandmeet.controller;

import com.project.uandmeet.dto.SearchResponseDto;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;



    @GetMapping("/board/search")
    public ResponseEntity<List<SearchResponseDto>> search(@RequestParam(value = "sort") String sort,
                                                         @RequestParam(value = "keyword") String keyword,
                                                         @RequestParam(value = "city") String city,
                                                         @RequestParam(value = "gu") String gu){

        List<SearchResponseDto> searchResponseDto = boardService.queryDslSearch(sort,keyword,city,gu);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(searchResponseDto);

    }


}
