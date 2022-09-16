package com.project.uandmeet.chat.repository;


import com.project.uandmeet.chat.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findByMemberIdAndChatRoomId(Long userId, Long ChatRoomId);
    List<Notice> findByMemberId(Long userId);
}
