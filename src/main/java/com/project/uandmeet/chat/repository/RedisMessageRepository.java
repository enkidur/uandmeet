package com.project.uandmeet.chat.repository;

import com.project.uandmeet.chat.dto.FindChatMessageDto;
import com.project.uandmeet.chat.model.ChatMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisMessageRepository extends CrudRepository<ChatMessage, Long> {

    List<FindChatMessageDto> findAllMessageByBoardId(String roomId);

    List<ChatMessage> findTop100MessageByBoardId(String boardId);
}
