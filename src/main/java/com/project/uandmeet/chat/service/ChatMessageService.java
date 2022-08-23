package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.dto.ChatMessageResponseDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.repository.ChatMessageRepository;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final MemberRepository memberRepository;

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
        Member member = memberRepository.findMemberByNickname(chatMessage.getNickname());
        // 도장 직기
        if (ChatMessage.MessageType.STAMP.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getMessage());
            chatMessage.setSender("[알림]");
            ChatMessageResponseDto chatMessageEnterResponseDto = new ChatMessageResponseDto(chatMessage);
            redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageEnterResponseDto);
        }
        // 회의록 작성
        if (ChatMessage.MessageType.RESULT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getMessage());
            chatMessage.setSender("[알림]");
            ChatMessageResponseDto chatMessageEnterResponseDto = new ChatMessageResponseDto(chatMessage);
            redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageEnterResponseDto);
        }
        // 안건 노출
        if (ChatMessage.MessageType.ISSUE.equals(chatMessage.getType())){
            chatMessage.setMessage(chatMessage.getMessage());
            chatMessage.setSender("[알림]");
            ChatMessageResponseDto chatMessageEnterResponseDto = new ChatMessageResponseDto(chatMessage);
            redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageEnterResponseDto);
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    public void save(ChatMessage chatMessage) {
        ChatMessage message = new ChatMessage();
        message.setType(chatMessage.getType());
        message.setRoomId(chatMessage.getRoomId());
        message.setNickname(chatMessage.getNickname());
        message.setSender(chatMessage.getSender());
        message.setMessage(chatMessage.getMessage());
        chatMessageRepository.save(message);
    }

    public ChatMessage getChatMessageByRoomId(String roomId) {
//        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() -1);
//        pageable = PageRequest.of(page, 150);
        return chatMessageRepository.findByRoomId(roomId);
    }
}
