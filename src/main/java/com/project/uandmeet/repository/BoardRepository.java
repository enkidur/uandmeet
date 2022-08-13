package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

@Repository
public  interface BoardRepository extends JpaRepository<Board, Long> ,QuerydslPredicateExecutor<Board>{
    List<Board> findAllByBoardTypeAndCategory(String boardType,String categoryName);

    List<Board> findByTitleContaining(String keyword); // Contiaining = Like

}
