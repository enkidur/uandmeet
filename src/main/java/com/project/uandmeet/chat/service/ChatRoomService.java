package com.project.uandmeet.chat.service;


import com.project.uandmeet.chat.dto.ChatRoomResponseDto;
import com.project.uandmeet.chat.dto.NicknameResponseDto;
import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.model.ChatRoomMember;
import com.project.uandmeet.chat.repository.ChatRoomMemberRepository;
import com.project.uandmeet.chat.repository.ChatRoomRepository;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    //레디스 저장소 사용
    //key hashKey value 구조
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final BoardRepository boardRepository;
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId 와 채팅룸 id 를 맵핑한 정보 저장

    // 채팅방 생성
    public Long createChatRoom(Long boardId,
                                   UserDetailsImpl userDetails) {
        if (!(memberRepository.findByUsername(userDetails.getUsername()).isPresent())){
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Board board = boardRepository.findBoardById(boardId);

        ChatRoom chatRoom = ChatRoom.builder()
                .id(boardId)
                .chatRoomName(board.getTitle())
                .boardId(boardId)
                .build();
        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }

    // 채팅방 나가기
    public ResponseEntity exitRoom(Long boardId,
                                   UserDetailsImpl userDetails){
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByMember_IdAndChatRoomId(userDetails.getMember().getId(),boardId);
        for (int i = 0; i < chatRoomMembers.size(); i++){
            ChatRoomMember chatRoomMember = chatRoomMembers.get(i);
            chatRoomMemberRepository.delete(chatRoomMember);
        }
        return ResponseEntity.ok("퇴장 완료");
    }

    // 개별 채팅방 조회
    public ChatRoomResponseDto getEachChatRoom(Long roomId, UserDetailsImpl userDetails) {
        if (!(memberRepository.findByUsername(userDetails.getUsername()).isPresent())){
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new CustomException(ErrorCode.ROOM_NOT_FOUND)
        );
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByChatRoom(chatRoom);
        int userCnt = chatRoomMembers.size();
        List<NicknameResponseDto> nicknameResponseDtos = new ArrayList<>();
        for (ChatRoomMember chatRoomMember : chatRoomMembers){
            Member member = chatRoomMember.getMember();
            NicknameResponseDto nicknameResponseDto = new NicknameResponseDto(member);
            nicknameResponseDtos.add(nicknameResponseDto);
        }

        return new ChatRoomResponseDto(chatRoom,userCnt,nicknameResponseDtos);
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
}
