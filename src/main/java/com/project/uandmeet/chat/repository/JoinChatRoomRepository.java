package com.project.uandmeet.chat.repository;

import com.project.uandmeet.chat.model.JoinChatRoom;
import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinChatRoomRepository extends JpaRepository<JoinChatRoom, Long> {
    List<JoinChatRoom> findJoinChatRoomsByMember(Member member);
    List<JoinChatRoom> findJoinChatRoomsByChatRoom_ChatRoomId(String chatRoomId);
    List<JoinChatRoom> findJoinChatRoomsByMember_Id(Long memberId);
    void deleteAllByMember(Member member);
    List<JoinChatRoom> deleteAllByChatRoom_ChatRoomId(String chatRoomId);
}
