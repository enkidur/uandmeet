package com.project.uandmeet.chat.repository;

import com.project.uandmeet.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findChatMessagesByChatroom_ChatRoomIdOrderByCreatedAt(String chatroomId);
    void deleteAllByChatroom_ChatRoomId(String chatRoomId);
}
