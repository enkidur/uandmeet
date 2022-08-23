package com.project.uandmeet.chat.repository;

import com.project.uandmeet.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findChatRoomById(Long roomId);

}
