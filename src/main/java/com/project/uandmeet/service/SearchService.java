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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BoardRepository boardRepository;

    public List<SearchResponseDto> queryDslSearch(String boardType,int page, int size, String sort, String keyword, String city, String gu) {

        if (page > 0)
            page = page - 1;
        else if (page <= 0)
            page = 0;

        // 제목만 검색
        if(sort.equals("title")){

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            QBoard qBoard = QBoard.board;

            BooleanBuilder builder = new BooleanBuilder();

            BooleanExpression exTitle = qBoard.title.contains(keyword);

            BooleanExpression exType = qBoard.boardType.contains(boardType);

            builder.and(exTitle);

            builder.and(exType);

            Page<Board> result = boardRepository.findAll(builder, pageable);

            List<SearchResponseDto> boardList = new ArrayList<>();

            result.stream().forEach(board -> {

                // 정보 공유일때
                if(board.getBoardType().equals("information")){

                    Long id = board.getId();
                    String bt = "information";
                    Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));

                    SearchResponseDto responseDto = new SearchResponseDto(board1,bt);

                    boardList.add(responseDto);

                }

                // 매칭 게시판일때
                else{

                    if(board.getCity().getCtpKorNmAbbreviation().equals(city) && board.getGu().getSigKorNm().equals(gu)){

                        Long id = board.getId();
                        Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));

                        SearchResponseDto responseDto = new SearchResponseDto(board1);
                        boardList.add(responseDto);
                    }

                }

            });
            return boardList;
        }

        // 내용만 검색
        if(sort.equals("content")){

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            QBoard qBoard = QBoard.board;

            BooleanBuilder builder = new BooleanBuilder();

            BooleanExpression exContent = qBoard.content.contains(keyword);

            BooleanExpression exType = qBoard.boardType.contains(boardType);

            builder.and(exContent);

            builder.and(exType);

            Page<Board> result = boardRepository.findAll(builder, pageable);

            List<SearchResponseDto> boardList = new ArrayList<>();

            result.stream().forEach(board -> {

                // 정보 공유일때
                if(board.getBoardType().equals("information")){
                    Long id = board.getId();
                    String bt = "information";
                    Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));

                    SearchResponseDto responseDto = new SearchResponseDto(board1,bt);

                    boardList.add(responseDto);
                }

                // 매칭 게시판일때
                else{

                    if(board.getCity().getCtpKorNmAbbreviation().equals(city) && board.getGu().getSigKorNm().equals(gu)){

                        Long id = board.getId();
                        Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));

                        SearchResponseDto responseDto = new SearchResponseDto(board1);

                        boardList.add(responseDto);

                    }

                }

            });
            return boardList;
        }

        //제목+내용 검색
        if (sort.equals("title_Content")) {

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            QBoard qBoard = QBoard.board;

            BooleanBuilder builder = new BooleanBuilder();

            BooleanExpression exTitle = qBoard.title.contains(keyword);

            BooleanExpression exContent = qBoard.content.contains(keyword);

            BooleanExpression exType = qBoard.boardType.contains(boardType);

            BooleanExpression exAll = exTitle.or(exContent);

            builder.and(exAll);

            builder.and(exType);

            Page<Board> result = boardRepository.findAll(builder, pageable);

            List<SearchResponseDto> boardList = new ArrayList<>();

            result.stream().forEach(board -> {

                // 정보 공유일때
                if(board.getBoardType().equals("information")){

                    Long id = board.getId();
                    String bt = "information";
                    Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));

                    SearchResponseDto responseDto = new SearchResponseDto(board1,bt);

                    boardList.add(responseDto);
                }

                // 매칭 게시판일때
                else{
                    if(board.getCity().getCtpKorNmAbbreviation().equals(city) && board.getGu().getSigKorNm().equals(gu)){

                        Long id = board.getId();
                        Board board1 = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("보드가 없습니다"));

                        SearchResponseDto responseDto = new SearchResponseDto(board1);

                        boardList.add(responseDto);
                    }
                }
            });


            return boardList;
        }

        return null;
    }

}