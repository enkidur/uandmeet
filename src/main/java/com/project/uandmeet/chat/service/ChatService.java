package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomService chatRoomService;
    private final MessageRepository messageRepository;

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

        // 채팅방 인원수 세팅
        chatMessage.setMemberCount(chatRoomService.getMemberCount(chatMessage.getRoomId()));

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
        }
        log.info("sender, sendMessage: {}, {}", chatMessage.getSender(), chatMessage.getMessage());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    // 채팅방의 마지막 150개 메세지를 페이징하여 리턴함
    public Page<ChatMessage> getChatMessageByRoomId(String roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "id" );
        pageable = PageRequest.of(page, 150, sort );
        return messageRepository.findByRoomIdOrderByIdDesc(roomId, pageable);
    }


}



