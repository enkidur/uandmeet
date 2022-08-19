package com.project.uandmeet.chat.repository;


import com.project.uandmeet.chat.model.InvitedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitedUsersRepository extends JpaRepository<InvitedUsers, Long> {


    void deleteByMemberIdAndBoardId(Long memberId, Long boardId);
    boolean existsByMemberIdAndBoardId(Long memberid, Long boardId);
    List<InvitedUsers> findAllByMemberId(Long memberId);
    void deleteAllByBoardId(Long boardId);
    void deleteByMemberId(Long memberId);
    List<InvitedUsers> findAllByBoardId(Long boardId);
    InvitedUsers findByMemberIdAndBoardId(Long id, Long id1);
    List<InvitedUsers> findAllByMemberIdAndReadCheck(Long memberId, Boolean readCheck);
    int countByBoardId(Long boardId);
    boolean existsByBoardId(Long id);
}
