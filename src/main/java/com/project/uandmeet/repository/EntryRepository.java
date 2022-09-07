package com.project.uandmeet.repository;

import com.project.uandmeet.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository <Entry, Long> {
    Optional<Entry> findByMemberAndBoard(Member member, Board board);

    Long countByMember(Member memberId);
    Long countByMemberAndCategory(Member memberId, Category category);

    List<Entry> findByMember(Member userId);
}
