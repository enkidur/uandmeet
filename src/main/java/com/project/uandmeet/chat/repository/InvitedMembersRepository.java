package com.project.uandmeet.chat.repository;


import com.project.uandmeet.chat.model.InvitedMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface InvitedMembersRepository extends JpaRepository<InvitedMembers, Long> {


    void deleteByMemberIdAndBoardId(Long memberId, Long boardId);
    boolean existsByMemberIdAndBoardId(Long member_id, Long boardId);
    List<InvitedMembers> findAllByMemberId(Long memberId);
    void deleteAllByBoardId(Long boardId);
    void deleteByMemberId(Long memberId);
    List<InvitedMembers> findAllByBoardId(Long boardId);
    InvitedMembers findByMemberIdAndBoardId(Long id, Long id1);
    List<InvitedMembers> findAllByMemberIdAndReadCheck(Long memberId, Boolean readCheck);
    int countByBoardId(Long boardId);
    boolean existsByBoardId(Long id);
}
