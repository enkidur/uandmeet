package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public  interface BoardRepository extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {
    List<Board> findAllByBoardTypeAndCategory(String boardType,String categoryName);

    Board findBoardById(Long boardId);
}
