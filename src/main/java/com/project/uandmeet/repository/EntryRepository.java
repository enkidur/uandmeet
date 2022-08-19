package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Entry;
import com.project.uandmeet.model.Liked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository <Entry, Long> {
    Optional<Entry> findByNicknameAndBoard(String nickname, Board board);
}
