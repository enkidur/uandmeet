package com.project.uandmeet.chat.repository;


import com.project.uandmeet.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByUsername(String username);
    ChatRoom findByRoomId(String roomId);
    void deleteByRoomId(String roomId);
}
