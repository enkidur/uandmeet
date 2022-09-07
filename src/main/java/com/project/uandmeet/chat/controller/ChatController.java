package com.project.uandmeet.chat.controller;


import com.project.uandmeet.chat.dto.ChatMessageRequestDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.service.ChatService;
import com.project.uandmeet.security.jwt.JwtAuthorizationFilter;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;
    private final MemberService memberService;

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageRequestDto messageRequestDto, @Header("Authorization") String token) {
        String nickname = jwtTokenProvider.decodeUsername(token);

        ChatMessage chatMessage = new ChatMessage(messageRequestDto, memberService);

        // 로그인 회원 정보로 대화명 설정
        chatMessage.setSender(nickname);

        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(chatMessage);
    }
}
