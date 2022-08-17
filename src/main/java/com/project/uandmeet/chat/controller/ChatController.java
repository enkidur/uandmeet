package com.project.uandmeet.chat.controller;


import com.fasterxml.jackson.core.JsonProcessingException;

import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.chat.dto.ChatMessageDto;
import com.project.uandmeet.chat.dto.UserDetailDto;
import com.project.uandmeet.chat.dto.UserinfoDto;
import com.project.uandmeet.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping({"/chat/message"})
    public void message(ChatMessageDto message, @Header("PK") Long pk) throws JsonProcessingException {
        chatService.save(message, pk);
    }

    //이전 채팅 기록 조회
    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public List<ChatMessageDto> getMessage(@PathVariable String roomId) {
        return chatService.getMessages(roomId);
    }

    //채팅방에 참여한 사용자 정보 조회
    @GetMapping("/chat/message/userinfo/{roomId}")
    @ResponseBody
    public List<UserinfoDto> getUserInfo(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.getUserinfo(userDetails, roomId);
    }

    //유저 정보 상세 조회 (채팅방 안에서)
    @GetMapping("/chat/details/{roomId}/{userId}")
    @ResponseBody
    public ResponseEntity<UserDetailDto> getUserDetails(@PathVariable String roomId, @PathVariable Long userId) {
        return chatService.getUserDetails(roomId,userId);
    }
}

