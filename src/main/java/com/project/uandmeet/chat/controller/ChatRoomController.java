package com.project.uandmeet.chat.controller;


import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.service.ChatRoomService;
import com.project.uandmeet.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    public List<ChatRoom> roomList() {
        List<ChatRoom> chatRooms = chatRoomService.findAllRoom();
        chatRooms.stream().forEach(room -> room.setMemberCount(chatRoomService.getMemberCount(room.getRoomId())));
        return chatRooms;
    }

    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(@PathVariable String roomId) {
        return roomId;
    }

    // 해당 채팅방의 메세지 조회
    @GetMapping("/messages/{roomId}")
    public Page<ChatMessage> getRoomMessage(@PathVariable String roomId, @PageableDefault Pageable pageable) {
        return chatService.getChatMessageByRoomId(roomId, pageable);
    }

}