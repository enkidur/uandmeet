package com.project.uandmeet.chat.repository;


import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findBySenderAndReceiver(Member sender, Member receiver);

    @Query(nativeQuery = true, value = "select * from chat_room cr where cr.senderid=:memberId or cr.receiverid=:memberId ORDER BY cr.modified_at desc")
    List<ChatRoom> findAllByMemberId(@Param("memberId") Long memberId);

}

