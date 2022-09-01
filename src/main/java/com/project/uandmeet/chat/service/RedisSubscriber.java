package com.project.uandmeet.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeet.chat.dto.ChatMessageDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisTemplate redisTemplate;


    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */

    public void sendMessage(String message) {
        try {

            // ChatMessage 객채로 맵핑
            ChatMessageDto roomMessage = objectMapper.readValue(message, ChatMessageDto.class);

            // Websocket 구독자에게 채팅 메시지 Send
            log.info("roomMessage.getMessage : {}", roomMessage.getMessage());
            log.info("roomMessage.getRoomId : {}", roomMessage.getBoardId());
            log.info("onMessage : {}", roomMessage.getType());
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getBoardId(), roomMessage);

            log.info("메세지 받기도 성공 ");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAILED_MESSAGE);
        }
    }
}
