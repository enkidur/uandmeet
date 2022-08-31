package com.project.uandmeet.chat.service;


import com.project.uandmeet.chat.dto.request.ChatRoomRequestDto;
import com.project.uandmeet.chat.dto.response.ChatRoomListDto;
import com.project.uandmeet.chat.dto.response.ChatRoomResponseDto;
import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.repository.ChatRoomRepository;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    //레디스 저장소 사용
    //key hashKey value 구조
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    private final ChatRoomRepository chatRoomRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId 와 채팅룸 id 를 맵핑한 정보 저장

    // 채팅방 생성
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto requestDto) {
        ChatRoom chatRoom = new ChatRoom(requestDto, memberService);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomResponseDto(chatRoom, memberService.findByNickname(requestDto.getNickname()));
    }

    // 유저가 들어가있는 전체 채팅방 조회
    public List<ChatRoomListDto> getAllChatRooms(UserDetailsImpl userDetails) {
        List<ChatRoomListDto> userChatRoom = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomRepository.findAllByOrderByCreatedAtDesc()) {
            System.out.println(chatRoom.getMemberList());
            for (int i = 0; i < chatRoom.getMemberList().size(); i++) {
                if (chatRoom.getMemberList().get(i).getNickname().equals(userDetails.getUsername())) {
                    userChatRoom.add(new ChatRoomListDto(chatRoom, chatRoom.getMemberList().get(i)));
                }
            }
        }
        return userChatRoom;
    }

    // 개별 채팅방 조회
    public ChatRoomResponseDto getEachChatRoom(Long id, Member member) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.ROOM_NOT_FOUND)
        );
        return new ChatRoomResponseDto(chatRoom, member);
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

    //채팅방 나가기
    @Transactional
    public ResponseEntity<?> outChatRoom(Long roomId, Member member) {
        Optional<ChatRoom> tmp = chatRoomRepository.findById(roomId);
        if (!tmp.isPresent()) {
            return ResponseEntity.badRequest().body(new CustomException(ErrorCode.ROOM_NOT_FOUND));
        }
        ChatRoom chatRoom = tmp.get();

        for (Member mem : chatRoom.getMemberList()) {
            if (mem.getId().equals(member.getId())) {
                chatRoom.getMemberList().remove(mem);
                break;
            }
        }
        chatRoomRepository.save(chatRoom);
        return ResponseEntity.ok().body("채팅방 나가기 성공!");
    }


}