package com.project.uandmeet.chat.controller;


import com.project.uandmeet.chat.dto.ChatRoomRequestDto;
import com.project.uandmeet.chat.dto.ChatRoomResponseDto;
import com.project.uandmeet.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final TokenValidator tokenValidator;

    @PostMapping("/room")
    @ResponseBody
    public ChatRoomResponseDto.ChatRoomData createRoom(@RequestBody ChatRoomRequestDto.Create create, @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        System.out.println("룸생성 시도");
        Member member = memberDetails.getMember();
        tokenValidator.memberIdCompareToken(create.getSenderId(), member.getId());
        System.out.println("토큰벨리데이터 성공");
        return chatRoomService.createChatRoom(create);

    }

    //채팅방 리스트 받아오기
    @GetMapping("/room/{memberId}")
    @ResponseBody
    public ChatRoomResponseDto.ChatRoomListData chatList(@PathVariable Long memberId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        System.out.println("채팅방 리스트 받아오기 시도");
        tokenValidator.memberIdCompareToken(memberId,memberDetails.getMember().getId());
        System.out.println("토큰벨리데이터 성공");
        return chatRoomService.findChatList(memberId);
    }

    //해당 채팅방 채팅내용 반환

    @GetMapping("/roomlist/{memberId}/chatRoom/{chatRoomId}")
    @ResponseBody
    public ChatRoomResponseDto.ChatMessageListData roomChatList(@PathVariable Long memberId, @PathVariable Long chatRoomId,
                                                                @RequestParam(value = "time",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime localDateTime,
                                                                @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        tokenValidator.memberIdCompareToken(memberId,memberDetails.getMember().getId());
        return chatRoomService.roomChatListService(memberId, chatRoomId, localDateTime);

    }
}
