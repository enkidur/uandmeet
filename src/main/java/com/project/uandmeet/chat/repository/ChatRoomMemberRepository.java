package com.project.uandmeet.chat.repository;

import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.model.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember,Long> {


    List<ChatRoomMember> findAllByChatRoom(ChatRoom chatRoom);

    List<ChatRoomMember> findAllByChatRoomId(Long chatRoomId);

    Optional<ChatRoomMember> findAllByChatRoom_Id (Long chatRoomId);

    List<ChatRoomMember> findAllByMember_Id(Long id);

    List<ChatRoomMember> findAllByMember_IdAndChatRoomId(Long userId, Long chatRoomId);
}
