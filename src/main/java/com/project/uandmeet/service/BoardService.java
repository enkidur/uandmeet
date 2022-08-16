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
public class BoardService {

    private final BoardRepository boardRepository;


    public List<SearchResponseDto> queryDslSearch(String sort, String keyword, String city, String gu) {

        if(sort.equals("title")){

            Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

            QBoard qBoard = QBoard.board;

            BooleanBuilder builder = new BooleanBuilder();

            BooleanExpression exTitle = qBoard.title.contains(keyword);

            builder.and(exTitle);

            Page<Board> result = boardRepository.findAll(builder, pageable);

            List<SearchResponseDto> boardList = new ArrayList<>();

            result.stream().forEach(board -> {
                if(board.getCity().equals(city) && board.getGu().equals(gu)){
                    String title = board.getTitle();
                    String content = board.getContent();
                    SearchResponseDto responseDto = new SearchResponseDto(title, content);

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

            BooleanExpression exContent = qBoard.content.contains(keyword);

            BooleanExpression exAll = exTitle.or(exContent);

            builder.and(exAll);

            Page<Board> result = boardRepository.findAll(builder, pageable);

            List<SearchResponseDto> boardList = new ArrayList<>();

            result.stream().forEach(board -> {
                if(board.getCity().equals(city) && board.getGu().equals(gu)) {
                    String title = board.getTitle();
                    String content = board.getContent();
                    SearchResponseDto responseDto = new SearchResponseDto(title, content);

                    boardList.add(responseDto);
                }
            });

            return boardList;
        }

        return null;
    }
}
