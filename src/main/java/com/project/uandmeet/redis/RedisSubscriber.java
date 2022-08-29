package com.project.uandmeet.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeet.chat.dto.response.EQMessageDto;
import com.project.uandmeet.chat.dto.response.MessageResponseDto;
import com.project.uandmeet.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
        System.out.println(publishMessage);
        if(publishMessage.split("\"")[1].equals("message")){
            MessageResponseDto dto= null;
            try {
                dto = objectMapper.readValue(publishMessage, MessageResponseDto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            messagingTemplate.convertAndSend("/queue/" + dto.getChatroomId(), dto);
        }
        if(publishMessage.split("\"")[1].equals("eqMessage")){
            EQMessageDto dto= null;
            try {
                dto = objectMapper.readValue(publishMessage, EQMessageDto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            messagingTemplate.convertAndSend("/queue"+dto.getChatroomId(),dto.getEqMessage());

        }

    }
}
