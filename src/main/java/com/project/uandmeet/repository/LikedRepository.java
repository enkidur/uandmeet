package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Liked;
import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedRepository extends JpaRepository<Liked, Long> {
    Optional<Liked> findByBoardAndMember(Board board, Member member);

    boolean existsByMemberAndBoard(Long member_id, Long board_id);
    Long countByBoard(Long Board_id);  //BoardId의 갯수를 센다.

    void deleteByMemberAndBoard(Long Member_id, Long Board_id);
}
