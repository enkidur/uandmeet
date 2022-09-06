package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>
{
    List<Comment> findByBoardOrderByCreatedAtDesc(Board board);
    Optional<Comment> findByIdAndBoard(Long id, Board board);
}