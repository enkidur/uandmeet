package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.repository.ChatRoomRepository;
import com.project.uandmeet.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    private final ChatRoomRepository chatRoomRepository;


    //채팅방생성
    @Transactional
    public ChatRoom createChatRoom() {
        String uuid = UUID.randomUUID().toString();
        ChatRoom chatRoom = new ChatRoom(uuid);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    // 채팅방 리스트
    public List<ChatRoom> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    // 회원이 입장한 채팅방 과 memberId 매핑 정보 저장
    public void setMemberEnterInfo(Long memberId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, Long.toString(memberId), roomId);
    }

    // 해당 회원이 들어가있는 채팅방 조회
    public String getMemberEnterRoomId(String memberId) {
        return hashOpsEnterInfo.get(ENTER_INFO, memberId);
    }

    // 회원과 매핑된 채팅방 삭제
    public void removeMemberEnterInfo(String memberId) {
        hashOpsEnterInfo.delete(ENTER_INFO, memberId);
    }

    // 채팅방 회원 수 조회
    public long getMemberCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방 입장
    public long plusMemberCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방 퇴장
    public long minusMemberCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }
}
