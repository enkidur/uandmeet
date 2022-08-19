package com.project.uandmeet.repository;

import com.project.uandmeet.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>
{
    List<Comment> findAllByBoard(Long id);

    Optional<Comment> findByIdAndBoard(Long id,Long boardId);
}
