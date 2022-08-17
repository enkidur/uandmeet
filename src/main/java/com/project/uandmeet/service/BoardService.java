package com.project.uandmeet.service;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Component
public class BoardService {

    private final BoardRepository boardRepository;
    public void minuslikecnt(Long postid) {
        Board board = boardRepository.findById(postid).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        board.minuslikecnt();
    }

    public void pluslikecnt(Long postid) {
        Board board = boardRepository.findById(postid).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        board.pluslikecnt();
    }
}
