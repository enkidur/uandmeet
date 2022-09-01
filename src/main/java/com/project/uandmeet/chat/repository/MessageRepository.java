package com.project.uandmeet.chat.repository;

import com.project.uandmeet.chat.dto.FindChatMessageDto;
import com.project.uandmeet.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    List<FindChatMessageDto> findTop100ByBoardIdOrderByIdDesc(String boardId);
}
