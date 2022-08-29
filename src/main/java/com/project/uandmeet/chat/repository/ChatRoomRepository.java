package com.project.uandmeet.chat.repository;

import com.project.uandmeet.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByChatRoomId(String chatRoomId);
    Optional<ChatRoom> deleteByChatRoomId(String chatRoomId);

}
