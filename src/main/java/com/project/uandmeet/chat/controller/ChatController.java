package com.project.uandmeet.chat.controller;



import com.project.uandmeet.chat.dto.ChatMessageRequestDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.model.Notice;
import com.project.uandmeet.chat.repository.ChatMessageRepository;
import com.project.uandmeet.chat.repository.ChatRoomRepository;
import com.project.uandmeet.chat.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessageSendingOperations sendingOperations;
    private final NoticeRepository noticeRepository;

    // 웹소켓으로 들어오는 메시지 발행 처리 -> 클라이언트에서는 /pub/templates/chat/message로 발행 요청
    @MessageMapping("/templates/chat/message")
    @Transactional
    public String message(ChatMessageRequestDto.Write message){

        System.out.println("채팅 보내는 클래스 들어옴");

        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId()).orElseThrow(()->new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        Member member = memberRepository.findById(message.getMemberId()).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        System.out.println(message.getModifiedAt());

        // 채팅보내는 시간을 형식에 맞춰 보내기 위한 코드
        String[] messageModifiedAt = message.getModifiedAt().split("T");
        String date = messageModifiedAt[0];
        String time = messageModifiedAt[1];
        String[] dateList = date.split("-");
        String year = dateList[0];
        String month = dateList[1];
        String day = dateList[2];
        System.out.println(year +"/"+month+"/"+day);
        String dayOfWeekStr ="";
        try{
            LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date));
            System.out.println("localDate : " + localDate);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            dayOfWeekStr = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN);
            System.out.println("오늘의 요일 : "+dayOfWeekStr);
        }catch (NumberFormatException e){
            LocalDate localDate = LocalDate.now();
            System.out.println("localDate : " + localDate);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            dayOfWeekStr = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN);
            System.out.println("오늘의 요일 : "+dayOfWeekStr);
        } catch (Exception e){
            // Exception 처리
        }

        String[] timeList = time.split(":");
        int hour = Integer.parseInt(timeList[0]);
        String min = timeList[1];
        System.out.println("hour : " +hour + " min : "+min);
        String messagedModifiedDate = year+"/" + month+"/"+day + " " + dayOfWeekStr;
        String messageModifiedTime;
        if(hour>12){messageModifiedTime = (hour-12)+":"+min+"PM";}
        else{messageModifiedTime = hour+":"+min+"AM";}
        // 채팅 데이터 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .message(message.getMessage())
                .chatRoom(chatRoom)
                .writer(member)
                .messageModifiedDate(messagedModifiedDate)
                .messageModifiedTime(messageModifiedTime)
                .build();

//        Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(),
                ChatMessageRequestDto.WriteSubscriber.builder()
                        .memberId(message.getMemberId())
                        .memberNickname(member.getNickname())
                        .messageModifiedDate(messagedModifiedDate)
                        .messageModifiedTime(messageModifiedTime)
                        .chatRoomId(message.getChatRoomId())
                        .message(message.getMessage())
                        .build());
        System.out.println("메시지 redis 발행 완료");


        // 룸 modifiedAt
        Long chatMessageId = chatMessageRepository.save(chatMessage).getId();
        System.out.println("chatMessageId : " + chatMessageId);
        ChatMessage getMessage = chatMessageRepository.findById(chatMessageId).orElseThrow(()->new CustomException(ErrorCode.CHATMESSAGE_NOT_FOUND));

        //chatRoom 수정시간 병경
        chatRoom.setModifiedAt(getMessage.getModifiedAt());
        System.out.println("채팅 수정시간 변경 완료");

        //알림 기능 추가
        System.out.println(message.getReceiverId()+"번 유저의 " +message.getChatRoomId()+"번 채팅방 알림 추가");
        if(!noticeRepository.findByMemberIdAndChatRoomId(message.getReceiverId(), message.getChatRoomId()).isPresent()){
            System.out.println("알림방 없어서 생성");
            Notice notice = Notice.builder()
                    .memberId(message.getReceiverId())
                    .chatRoomId(message.getChatRoomId())
                    .count(1)
                    .build();
            noticeRepository.save(notice);
        }else{
            System.out.println("알림방 있어서 갯수 추가");
            Notice notice = noticeRepository.findByMemberIdAndChatRoomId(message.getReceiverId(), message.getChatRoomId()).orElseThrow(()-> new IllegalArgumentException("알림 데이터 없음"));
            System.out.println("업데이트 전 알림수" + notice.getCount());
            notice.updateCount();
            System.out.println("업데이트 후 알림수" + notice.getCount());
            noticeRepository.save(notice);
        }

        return "킹준호";
    }

}
