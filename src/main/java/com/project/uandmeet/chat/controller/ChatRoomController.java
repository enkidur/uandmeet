package com.project.uandmeet.chat.controller;

import com.project.uandmeet.chat.dto.request.ChatRoomRequestDto;
import com.project.uandmeet.chat.dto.response.ChatRoomListDto;
import com.project.uandmeet.chat.dto.response.ChatRoomResponseDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.service.ChatMessageService;
import com.project.uandmeet.chat.service.ChatRoomService;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;


    // 채팅방 생성
    @PostMapping("/rooms")
    public ChatRoomResponseDto ChatRoomResponseDto(@RequestBody ChatRoomRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        requestDto.setNickname(userDetails.getMember().getNickname());
        ChatRoomResponseDto chatRoom = chatRoomService.createChatRoom(requestDto);
        return chatRoom;
    }

    // 전체 채팅방 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoomListDto> getAllChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getAllChatRooms(userDetails);
    }

    //채팅방 나가기
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<?> outChatRoom(@PathVariable Long roomId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.outChatRoom(roomId, userDetails.getMember());
    }

    // 채팅방 상세 조회
    @GetMapping("/rooms/{roomId}")
    public ChatRoomResponseDto getEachChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getEachChatRoom(roomId,userDetails.getMember());
    }

    // 채팅방 내 메시지 전체 조회
    @GetMapping("/rooms/{roomId}/messages")
    public Page<ChatMessage> getEachChatRoomMessages(@PathVariable String roomId, @PageableDefault Pageable pageable) {
        return chatMessageService.getChatMessageByRoomId(roomId, pageable);
    }
}
