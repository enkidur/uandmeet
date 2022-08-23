package com.project.uandmeet.chat.controller;


import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.chat.dto.ChatListMessageDto;
import com.project.uandmeet.chat.dto.UserDto;
import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.repository.ChatRoomRepository;
import com.project.uandmeet.dto.LoginInfo;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    // 모든 채팅창 조회
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return chatRoomRepository.findAllRoom();
    }

    // 특정 채팅방 입장
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public void roomInfo(@PathVariable Long boardId) {
        chatRoomRepository.roomInfo(String.valueOf(boardId));
    }

    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public void createRoom( Board board, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomRepository.createChatRoom(board, userDetails);
    }

//    @GetMapping("/user")
//    @ResponseBody
//    public LoginInfo getUserInfo() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String name = auth.getName();
//        HttpHeaders headers = new HttpHeaders();
//        return LoginInfo.builder()
//                .name(name)
//                .token()
//                .build();
//    }
}