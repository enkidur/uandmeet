package com.project.uandmeet.repository;

import com.project.uandmeet.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository <Entry, Long> {
    Optional<Entry> findByMemberAndBoard(Member member, Board board);
    Optional<Entry> findByMemberAndBoard(String member_id, Board board_id);

    boolean existsByMemberAndBoard(Board board, Member member);
    int countByMemberAndIsMatchingAndBoard(Member member, boolean isMatching);
    Long countByBoard(Long Board_id);  //BoardId의 갯수를 센다.
    Long countByMember(Member memberId);
    Long countByBoard(String category);
    Long countByMemberAndCategory(Member memberId, Category category);
    void deleteByMemberAndBoard(Long Member_id, Long Board_id);

    Long[] findByBoard(Category category);
    List<Entry> findByMember(Member userId);
}
