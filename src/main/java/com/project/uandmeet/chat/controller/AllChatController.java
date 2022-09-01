package com.project.uandmeet.chat.controller;

import com.project.uandmeet.chat.dto.ChatMessageDto;
import com.project.uandmeet.chat.dto.FindChatMessageDto;
import com.project.uandmeet.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AllChatController {

    private final ChatService chatService;

    // 메세지 보내기
    @MessageMapping({"/chat/message"})
    public void message(ChatMessageDto messageDto, @Header("Authorization") String token) {
        log.info("요청 메서드 [message] /chat/message");
        chatService.save(messageDto, token);
    }

    //이전 채팅 기록 조회
    @GetMapping("/chat/message/{boardId}")
    @ResponseBody
    public List<FindChatMessageDto> getAllMessage(@PathVariable String boardId){
        return chatService.getAllMessage(boardId);
    }
}
