package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.dto.ChatMessageDto;
import com.project.uandmeet.chat.dto.FindChatMessageDto;
import com.project.uandmeet.chat.dto.SenderDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.repository.MessageRepository;
import com.project.uandmeet.chat.repository.RedisMessageRepository;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    //의존성 주입
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    private final RedisPublisher redisPublisher;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageRepository messageRepository;
    private final RedisMessageRepository redisMessageRepository;
    @Transactional
    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());

        // 회원 정보값을 토큰으로 찾아오기
        Member member = CommonUtil.getMemberByToken(token, jwtTokenProvider);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

        // 메세지 보내는 사람의 정보값 넣기
        messageDto.setMember(SenderDto.builder()
                        .id(member.getId())
                        .profile(member.getProfile())
                        .nickname(member.getNickname())
                .build());
        messageDto.setCreatedAt(now);

        // DB 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .message(messageDto.getMessage())
                .boardId(messageDto.getBoardId())
                .type(messageDto.getType())
                .member(member)
                .createdAt(now)
                .build();

        messageRepository.save(chatMessage);
        messageDto.setId(chatMessage.getId());
        redisPublisher.publishsave(messageDto);

    }

    // 이전 채팅 기록 조회
    public List<FindChatMessageDto> getAllMessage(String boardId) {
        HashOperations<String, String, List<FindChatMessageDto>> opsHashChatMessage = redisTemplate.opsForHash();


        List<FindChatMessageDto> chatMessageDtoList = opsHashChatMessage.get(CHAT_MESSAGE, boardId);

        if (chatMessageDtoList!= null && chatMessageDtoList.size() > 100) {
            //from redis
            return chatMessageDtoList;
        }
        //from mysql

        // redis 에서 가져온 메세지 리스트의 값이 null 일때  Mysql db에서 데이터를 불러와 레디스에 저장후 리턴
        List<FindChatMessageDto> chatMessages = messageRepository.findTop100ByBoardIdOrderByIdDesc(boardId);

        redisTemplate.opsForHash().put(CHAT_MESSAGE, boardId, chatMessages);
        redisTemplate.expire(CHAT_MESSAGE,10, TimeUnit.SECONDS);

        return chatMessages;
    }


}



