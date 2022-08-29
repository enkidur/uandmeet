package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.dto.response.ChatRoomMemberInofoResponseDto;
import com.project.uandmeet.chat.dto.response.ChatRoomResponseDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.model.JoinChatRoom;
import com.project.uandmeet.chat.repository.ChatMessageRepository;
import com.project.uandmeet.chat.repository.ChatRoomRepository;
import com.project.uandmeet.chat.repository.JoinChatRoomRepository;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JoinChatRoomRepository joinChatRoomRepository;


    // 해당 유저의 채팅방 목록 불러오기

    public List<ChatRoomResponseDto> readChatRoomList(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Member otherMember;

        List<ChatRoom> chatRoomList = new ArrayList<>();
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        // 해당 유저의 JoinChatRoom 리스트를 불러온다
        List<JoinChatRoom> joinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByMember(member);

        // 채팅방 리스트를 모두 가져온다.
        for(JoinChatRoom joinChatRoom : joinChatRoomList) {
            if(!joinChatRoom.isJoinChatRoomOut()) {
                chatRoomList.add(joinChatRoom.getChatRoom());
            }
        }

        for(ChatRoom chatRoom : chatRoomList) {
            otherMember = null;
            // 상대유저의 닉네임을 찾아내야한다.
            List<JoinChatRoom> foundJoinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByChatRoom_ChatRoomId(chatRoom.getChatRoomId());
            for(JoinChatRoom joinChatRoom : foundJoinChatRoomList) {
                if(joinChatRoom.getMember().getId()!=userDetails.getMember().getId()) {
                    otherMember = joinChatRoom.getMember();
                }
            }
            if(otherMember==null){
                ChatMessage lastMessage;
                String createdAt;
                try {
                    lastMessage = chatMessageRepository.findById(chatRoom.getLastMessageId()).orElse(null);
                    if(lastMessage==null)
                        createdAt="";
                    else
                        createdAt = extractDateFormat(lastMessage.getCreatedAt());
                }catch(Exception e){
                    lastMessage=new ChatMessage();
                    lastMessage.setCreatedAt("");
                    createdAt = "";
                }
                // Builder Annotation 사용
                ChatRoomResponseDto chatRoomResponseDto = ChatRoomResponseDto.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .senderNickname("탈퇴한 사용자")
                        .image(null)
                        .createdAt(createdAt)
                        .lastMessage(lastMessage.getMessage())
                        .otherId(null)
                        .build();
                chatRoomResponseDtoList.add(chatRoomResponseDto);
            }
            else{
                ChatMessage lastMessage;
                String createdAt;
                try {
                    lastMessage = chatMessageRepository.findById(chatRoom.getLastMessageId()).orElse(null);
                    if(lastMessage==null)
                        createdAt="";
                    else
                        createdAt = extractDateFormat(lastMessage.getCreatedAt());
                }catch(Exception e){
                    lastMessage=new ChatMessage();
                    lastMessage.setCreatedAt("");
                    createdAt = "";
                }
                // Builder Annotation 사용
                ChatRoomResponseDto chatRoomResponseDto = ChatRoomResponseDto.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .senderNickname(otherMember.getNickname())
                        .image(otherMember.getProfile())
                        .createdAt(createdAt)
                        .lastMessage(lastMessage.getMessage())
                        .otherId(otherMember.getId())
                        .build();
                chatRoomResponseDtoList.add(chatRoomResponseDto);
            }
        }
        return chatRoomResponseDtoList;
    }

    // 1:1 채팅방 만들기
    // 중복 채팅방이 없으면 새로운 채팅방을 만들고 그 채팅방 ID를 프론트엔드에게 전달한다.
    // 중복 채팅방이 있으면 기존의 채팅방 ID를 찾아 프론트엔드에게 전달한다.
    @Transactional
    public String createChatRoom(UserDetailsImpl userDetails, Long otherId) {
        // 채팅 기록이 있는지 확인 - 기존에 있는 채팅인지 아닌지 판별하기(유저 두명이 다 있는 채팅방인지 판별해야한다.)
        Member member = userDetails.getMember();
        Member otherMember = memberRepository.findById(otherId).orElseThrow(
                () -> new IllegalArgumentException("상대 유저가 존재하지 않습니다.")
        );

        // 클라이언트에게 전달할 채팅방 ID
        String chatRoomId = "";

        // 중복 채팅방 확인

        List<JoinChatRoom> joinChatRoomMemberList = joinChatRoomRepository.findJoinChatRoomsByMember_Id(member.getId());

        for(JoinChatRoom joinChatRoom : joinChatRoomMemberList) {
            String tempChatRoomId = joinChatRoom.getChatRoom().getChatRoomId();
            List<JoinChatRoom> joinChatRoomOtherList = joinChatRoomRepository.findJoinChatRoomsByChatRoom_ChatRoomId(tempChatRoomId);
            boolean exist = false;
            for(JoinChatRoom tempJoinChatRoom : joinChatRoomOtherList) {
                if (tempJoinChatRoom.getMember().getId().equals(otherId)) {
                    exist = true;
                } else {
                    tempJoinChatRoom.isOut(false);
                    joinChatRoomRepository.save(tempJoinChatRoom);
                }
            }
            if(exist)
                return tempChatRoomId;
        }
        System.out.println("기존 채팅방이 존재하지 않을 경우");
        // 중복 채팅방이 없다면 채팅방을 새로 만들어준다.
        // 채팅방 생성과 동시에 JoinChatRoom에 두명의 유저가 추가된다.
        ChatRoom chatRoom = new ChatRoom();
        JoinChatRoom joinChatRoomMemberTwo = new JoinChatRoom(member, chatRoom, false);
        JoinChatRoom joinChatRoomOtherMemberTwo = new JoinChatRoom(otherMember, chatRoom, false);

            /* JPA 관련 Hibernate 에러
              ## Error
              : object references an unsaved transient instance - save the transient instance before flushing
              ## 이유?
              : FK 로 사용되는 ChatRoom의 DB 컬럼값이 없는 상태에서 데이터를 넣으려다 발생한 에러이다.
              ## 해결방법?
              : 연관 관계 매핑해줄 때 사용하는 @ManyToOne, @OneToOne, @OneToMany 어노테이션에 cascade 옵션을 설정해준다.
              cascade 는 "영속성 전이" 라고 하는 개념인데 특정 엔티티를 영속화 할 때 연관된 엔티티도 함께 영속화 한다.
              저장할 때만 사용하려면 cascade = CascadeType.PERSIST 로 설정해주면 되며, 전체 적용인 CascadeType.ALL 로 설정하여 해결한다.
              joinChatRoom Entity => @ManyToOne(cascade = CascadeType.ALL)
                                     private ChatRoom chatRoom;
             */
        joinChatRoomRepository.save(joinChatRoomMemberTwo);
        joinChatRoomRepository.save(joinChatRoomOtherMemberTwo);

        List<JoinChatRoom> addJoinChatRoomList = new ArrayList<>();
        addJoinChatRoomList.add(joinChatRoomMemberTwo);
        addJoinChatRoomList.add(joinChatRoomOtherMemberTwo);
        chatRoom.addJoinChatRooms(addJoinChatRoomList);

        chatRoomRepository.save(chatRoom);

        chatRoomId = chatRoom.getChatRoomId();

        return chatRoomId;
    }

    // 채팅방 유저정보 조회
    public ChatRoomMemberInofoResponseDto readChatRoomMemberInfo(UserDetailsImpl userDetails, String chatRoomId) {
        Long memberId = userDetails.getMember().getId();
        List<JoinChatRoom> joinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByChatRoom_ChatRoomId(chatRoomId);
        String otherNickname = "";
        String otherProfileImg = "";
        for(JoinChatRoom joinChatRoom : joinChatRoomList) {
            if(!joinChatRoom.getMember().getId().equals(memberId)) {
                Long otherId = joinChatRoom.getMember().getId();
                Member otherMember = memberRepository.findById(otherId).orElseThrow(
                        () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
                );
                otherNickname = otherMember.getNickname();
                otherProfileImg = otherMember.getProfile();
            }
        }
        if(otherNickname.equals("")){
            return ChatRoomMemberInofoResponseDto.builder()
                    .otherNickname("탈퇴한 사용자")
                    .otherProfile(null)
                    .memberId(memberId)
                    .build();
        }

        return ChatRoomMemberInofoResponseDto.builder()
                .otherNickname(otherNickname)
                .otherProfile(otherProfileImg)
                .memberId(memberId)
                .build();
    }

    // 채팅방 메시지 날짜&시간 형식을 만드는 메서드
    private String extractDateFormat(String messageDate) {

        String result = "";
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedNow = simpleDateFormat.format(now).toString();
        String[] curFormattedSplitByDash = formattedNow.split("-");
        String curYear = curFormattedSplitByDash[0];
        String curMonth = curFormattedSplitByDash[1];
        String curDay = curFormattedSplitByDash[2].split(" ")[0];
        String curHour = curFormattedSplitByDash[2].split(" ")[1].split(":")[0];
        String curMinute = curFormattedSplitByDash[2].split(" ")[1].split(":")[1];
        System.out.println("----------------- 현재 날짜 & 시각 -----------------");
        System.out.println(curYear + "년 " + curMonth + "월 " + curDay + "일 " + curHour + "시 " + curMinute + "분 ");

        String[] formattedSplitByDash = messageDate.split("-");
        String year = formattedSplitByDash[0];
        String month = formattedSplitByDash[1];
        String day = formattedSplitByDash[2].split(" ")[0];
        String hour = formattedSplitByDash[2].split(" ")[1].split(":")[0];
        String minute = formattedSplitByDash[2].split(" ")[1].split(":")[1];

        // 1) 오늘인지 아닌지
        if(curMonth.equals(month) && curDay.equals(day)) {
            // 2) 오전인지 오후 인지
            if(Integer.parseInt(hour) < 12) {
                result = "오전 " + hour + ":" + minute;
            } else {
                Integer afterHour = (Integer.parseInt(hour) - 12);
                result = "오후 " + afterHour + ":" + minute;
            }
        } else if (curMonth.equals(month) && ((Integer.parseInt(curDay) - 1) == Integer.parseInt(day))) {
            // 3) 하루 전인지 아닌지
            result = "하루 전";
        } else {
            // 4) 하루 전이 아니라면
            result = month + "월 " + day + "일";
        }
        return result;
    }

    // 채팅방 나가기
    public void outChatRoom(UserDetailsImpl userDetails, String chatRoomId) {
        Long memberId = userDetails.getMember().getId();
        List<JoinChatRoom> joinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByChatRoom_ChatRoomId(chatRoomId);
        int count = 0;
        // 채팅방 둘다 아웃인 경우 즉, false일 경우 채팅방 삭제
        for(JoinChatRoom tempJoinChatRoom : joinChatRoomList) {
            if(tempJoinChatRoom.isJoinChatRoomOut()) {
                count ++;
            }
        }
        if(count==2) {
            // JoinChatRoom 삭제
            joinChatRoomRepository.deleteAllByChatRoom_ChatRoomId(chatRoomId);
            // 채팅방메시지 모두 삭제
            chatMessageRepository.deleteAllByChatroom_ChatRoomId(chatRoomId);
            // 채팅방 삭제
            chatRoomRepository.deleteByChatRoomId(chatRoomId);
        }

        // 채팅방 둘다 아웃이 아닌 경우 즉, 한쪽이라도 true일 경우 joinChatroom 채팅방 아웃 칼럼을 수정하여 저장
        for(JoinChatRoom tempJoinChatRoom : joinChatRoomList) {
            if(tempJoinChatRoom.getMember().getId().equals(memberId)) {
                tempJoinChatRoom.isOut(true);
                joinChatRoomRepository.save(tempJoinChatRoom);
            }
        }
    }
}






