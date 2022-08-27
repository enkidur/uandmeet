package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public  interface BoardRepository extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {

    Board findBoardById(Long boardId);

    Page<Board> findAllByBoardTypeAndCategory(String boardType, String Category, Pageable pageable);

    Page<Board> findAllByBoardType(String boardType, Pageable pageable);
}
