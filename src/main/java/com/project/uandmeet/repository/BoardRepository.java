package com.project.uandmeet.repository;

import com.project.uandmeet.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public  interface BoardRepository extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {

    Board findBoardById(Long boardId);
    Page<Board> findAllByBoardTypeAndCategory(String boardType, Category Category, Pageable pageable);
    Page<Board> findAllByBoardTypeAndCategoryAndCity(String boardType, Category category, Siarea city, Pageable pageable);
    Page<Board> findAllByBoardTypeAndCategoryAndCityAndGu(String boardType, Category Category, Pageable pageable,Siarea City, Guarea Gu);

    Page<Board> findAllByBoardType(String boardType, Pageable pageable);
    Page<Board> findAllByBoardTypeAndCity(String boardType, Pageable pageable,Siarea City);
    Page<Board> findAllByBoardTypeAndCityAndGu(String boardType, Pageable pageable,Siarea City, Guarea Gu);

    List<Board> findByMemberAndBoardType(Member member, String boardType);
    Page<Board> findByMemberAndBoardType(Member member,  String boardType, Pageable pageable);
    Long countByMemberAndAndBoardType(Member member, String boardType);
    List<Board> findByBoardTypeOrderByLikeCount(String boardType);
    Slice<Board> findByBoardTypeOrderByLikeCount(String boardType, Pageable pageable);
    List<Board> findByBoardTypeAndCategoryOrderByLikeCount(String boardtype, Category category);
    Slice<Board> findByBoardTypeAndCategoryOrderByLikeCount(String boardtype, Category category, Pageable pageable);


}
