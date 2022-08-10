package com.project.uandmeet.chat.repository;


import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findByChatRoomIdOrderByModifiedAt(Long chatroomId);

    Optional<ChatMessage> findByChatRoomAndMessageModifiedDate(ChatRoom chatRoom, String messagedModifiedDate);
}
