package com.project.uandmeet.chat.controller;

import com.project.uandmeet.chat.dto.ChatRoomResponseDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.service.ChatMessageService;
import com.project.uandmeet.chat.service.ChatRoomService;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;


    // 채팅방 생성
    @PostMapping("/rooms")
    public HashMap<String, Object> ChatRoomResponseDto(@PathVariable Long meetingId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        Long roomId = chatRoomService.createChatRoom(meetingId,userDetails);
        HashMap<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        return result;
    }

    // 채팅방 나가기
    @PostMapping("/rooms/exit")
    public ResponseEntity exitRoom(@PathVariable Long meetingId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.exitRoom(meetingId, userDetails);
    }

    // 채팅방 상세 조회
    @GetMapping("/rooms/{roomId}")
    public ChatRoomResponseDto getEachChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getEachChatRoom(roomId,userDetails);
    }

    // 채팅방 내 메시지 전체 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ChatMessage getEachChatRoomMessages(@PathVariable String roomId) {
        return chatMessageService.getChatMessageByRoomId(roomId);
    }
}
