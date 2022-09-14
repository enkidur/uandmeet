package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Comment;
import com.project.uandmeet.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>
{
    List<Comment> findAllByBoard(Long id);

    Optional<Comment> findByIdAndBoard(Long id, Long boardId);
    Page<Comment> findAllByMemberAndBoardType(Member member, String boardType,Pageable pageable);

    Long countByMemberAndBoardType(Member member, String boardType);

    List<Comment> findByBoardOrderByCreatedAtDesc(Board board);
    Optional<Comment> findByIdAndBoard(Long id, Board board);

    List<Comment> findByMember(Member member);
}