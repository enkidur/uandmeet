package com.project.uandmeet.service;

import com.project.uandmeet.dto.SearchResponseDto;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.QBoard;
import com.project.uandmeet.repository.BoardRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BoardRepository boardRepository;


    public List<SearchResponseDto> queryDslSearch(int page, int size, String sort, String keyword, String city, String gu) {

        if(sort.equals("title")){

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            QBoard qBoard = QBoard.board;

            BooleanBuilder builder = new BooleanBuilder();

            BooleanExpression exTitle = qBoard.title.contains(keyword);

            builder.and(exTitle);

            Page<Board> result = boardRepository.findAll(builder, pageable);

            List<SearchResponseDto> boardList = new ArrayList<>();

            result.stream().forEach(board -> {
                if(board.getCity().equals(city) && board.getGu().equals(gu)){
                    Long id = board.getId();
                    Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));
                    // 검색결과에 매칭,정보가 모두 포함되기에 나눠줌
                    SearchResponseDto responseDto = new SearchResponseDto(board1);
//                    // 게시판 종류가 매칭 게시판일때 && 검색 결과중 BoardType이 matching인 것만 긁어온다.
//                    if(boardType.equals("matching") && board1.getBoardType().equals("matching")){ // 게시판 종류가 매칭 게시판일 경우
//                        SearchResponseDto responseDto = new SearchResponseDto(board1);
//                        boardList.add(responseDto);
//                    }
//
//                    // 게시판 종류가 정보공유 게시판일때 && 검색 결과중 BoardType이 information인 것만 긁어온다.
//                    else if(boardType.equals("information") && board1.getBoardType().equals("information")){ // 게시판 종류가 정보공유 게시판일 경우
//                            SearchResponseDto responseDto = new SearchResponseDto(board1);
//                            boardList.add(responseDto);
//                    }

                    boardList.add(responseDto);

                }
            });
            return boardList;
        }


        if (sort.equals("title_Content")) {

            Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

            QBoard qBoard = QBoard.board;

            BooleanBuilder builder = new BooleanBuilder();

            BooleanExpression exTitle = qBoard.title.contains(keyword);

            BooleanExpression exContent = qBoard.centent.contains(keyword);

            BooleanExpression exAll = exTitle.or(exContent);

            builder.and(exAll);

            Page<Board> result = boardRepository.findAll(builder, pageable);

            List<SearchResponseDto> boardList = new ArrayList<>();

            result.stream().forEach(board -> {
                if(board.getCity().equals(city) && board.getGu().equals(gu)) {
                    Long id = board.getId();
                    Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));

                    SearchResponseDto responseDto = new SearchResponseDto(board1);

                    boardList.add(responseDto);
                }
            });

            return boardList;
        }

        return null;
    }

}