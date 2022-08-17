package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
