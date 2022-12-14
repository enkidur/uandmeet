package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.dto.ChatMessageDto;
import com.project.uandmeet.chat.dto.FindChatMessageDto;
import com.project.uandmeet.chat.dto.SenderDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.repository.MessageRepository;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    //레디스 저장소 사용
    //key hashKey value 구조
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    //의존성 주입
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    private final RedisPublisher redisPublisher;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageRepository messageRepository;

    // 채팅룸에 입장한 클라이언트의 sessionId 와 채팅룸 id 를 맵핑한 정보 저장
    public static final String ENTER_INFO = "ENTER_INFO";

    // destination 정보에서 roomId 추출
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    // 유저가 입장한 채팅방 ID 와 유저 세션 ID 맵핑 정보 저장
    //Enter라는 곳에 sessionId와 roomId를 맵핑시켜놓음
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방 ID 삭제
    //한 유저는 하나의 룸 아이디에만 맵핑되어있다!
    // 실시간으로 보는 방은 하나이기 떄문이다.
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    @Transactional
    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());

        // 유저 정보값을 토큰으로 찾아오기
        Member member = CommonUtil.getUserByToken(token, jwtTokenProvider);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

        // 메세지 보내는 사람의 정보값 넣기
        messageDto.setMember(SenderDto.builder()
                        .id(member.getId())
                        .profile(member.getProfile())
//                        .role(member.getRole())
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

        // ChatRoom chatRoom2 = roomRepository.findByRoomId(boardId);
        redisTemplate.opsForHash().put(CHAT_MESSAGE, boardId, chatMessages);
        redisTemplate.expire(CHAT_MESSAGE,10, TimeUnit.SECONDS);

        return chatMessages;
    }


}



