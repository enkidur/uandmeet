package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.repository.ChatMessageRepository;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberService memberService;

    // destination 정보에서 roomId 추출
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessage chatMessage) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    public void save(ChatMessage chatMessage) {
        ChatMessage message = new ChatMessage();
        message.setType(chatMessage.getType());
        message.setRoomId(chatMessage.getRoomId());
        message.setMember(memberService.findByNickname(chatMessage.getNickname()));
        message.setNickname(chatMessage.getNickname());
        message.setSender(chatMessage.getSender());
        message.setMessage(chatMessage.getMessage());
        message.setCreatedAt(chatMessage.getCreatedAt());
        chatMessageRepository.save(message);
    }

    public Page<ChatMessage> getChatMessageByRoomId(String roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() -1);
        pageable = PageRequest.of(page, 150);
        return chatMessageRepository.findByRoomId(roomId, pageable);
    }
}