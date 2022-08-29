package com.project.uandmeet.chat.controller;

import com.project.uandmeet.chat.dto.request.ChatRoomRequestDto;
import com.project.uandmeet.chat.dto.response.ChatRoomMemberInofoResponseDto;
import com.project.uandmeet.chat.dto.response.ChatRoomResponseDto;
import com.project.uandmeet.chat.service.ChatRoomService;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 해당 유저의 채팅방 목록 불러오기
    @GetMapping("/chatroom/get")
    public List<ChatRoomResponseDto> readChatRoomList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.readChatRoomList(userDetails);
    }

    // 1:1 채팅방 생성하기
    @PostMapping("/chatroom/create")
    public ResponseEntity<?> createChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ChatRoomRequestDto requestDto) {
        System.out.println("사용자 유저 아이디 : " + userDetails.getMember().getId());
        System.out.println("상대방 유저 아이디 : " + requestDto.getOtherId());
        String chatRoomId = chatRoomService.createChatRoom(userDetails, requestDto.getOtherId());
        Map<String, Object> result = new HashMap<>();
        result.put("result", "ok");
        if(chatRoomId.equals("")) {
            result.put("msg", "이미 채팅방이 존재합니다.");
            result.put("chatRoomId", chatRoomId);
        } else {
            result.put("msg", "성공적으로 채팅방이 개설되었습니다.");
            result.put("chatRoomId", chatRoomId);
        }

        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/chatroom/user/{chatRoomId}")
    public ChatRoomMemberInofoResponseDto readChatRoomUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String chatRoomId) {
        return chatRoomService.readChatRoomMemberInfo(userDetails, chatRoomId);
    }

    // 채팅 방 나갈 때 로직
    @GetMapping("/chatroom/out/{chatRoomId}")
    public ResponseEntity<?> outChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String chatRoomId) {
        chatRoomService.outChatRoom(userDetails, chatRoomId);
        return ResponseEntity.status(200).body("채팅룸 나가기 완료!");
    }
}
