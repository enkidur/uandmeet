package com.project.uandmeet.chat.controller;

import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.chat.dto.ChatListMessageDto;
import com.project.uandmeet.chat.repository.ChatRoomRepository;
import com.project.uandmeet.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    // 내 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public ChatListMessageDto room(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        return chatRoomRepository.findAllRoom(member);
    }

    // 특정 채팅방 입장
    @PostMapping("/room/{postId}")
    @ResponseBody
    public String roomInfo(@PathVariable Long postId) {
        return String.valueOf(postId);
    }



}