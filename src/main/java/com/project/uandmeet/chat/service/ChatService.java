//package com.project.uandmeet.chat.service;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//
//import com.project.uandmeet.auth.UserDetailsImpl;
//import com.project.uandmeet.chat.dto.ChatMessageDto;
//import com.project.uandmeet.chat.dto.UserDetailDto;
//import com.project.uandmeet.chat.dto.UserinfoDto;
//import com.project.uandmeet.chat.model.*;
//import com.project.uandmeet.chat.repository.*;
//import com.project.uandmeet.exception.CustomException;
//import com.project.uandmeet.exception.ErrorCode;
//import com.project.uandmeet.model.Member;
//import com.project.uandmeet.repository.BoardRepository;
//import com.project.uandmeet.repository.LikedRepository;
//import com.project.uandmeet.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//@Service
//@RequiredArgsConstructor
//public class ChatService {
//
//    private final RedisPublisher redisPublisher;
//    private final ChatRoomRepository chatRoomRepository;
//    private final ChatMessageRepository chatMessageRepository;
//    private final MemberRepository memberRepository;
//    private final ChatMessageJpaRepository chatMessageJpaRepository;
//    private final InvitedUsersRepository invitedUsersRepository;
//    private final ChatRoomJpaRepository chatRoomJpaRepository;
//    private final ResignChatRoomJpaRepository resignChatRoomJpaRepository;
//    private final BoardRepository boardRepository;
//    private final LikedRepository likeRepository;
//    private final ResignChatMessageJpaRepository resignChatMessageJpaRepository;
//
//
//    @Transactional
//    public void save(ChatMessageDto messageDto, Long pk) throws JsonProcessingException {
//        // 토큰에서 유저 아이디 가져오기
//        Member member = memberRepository.findById(pk).orElseThrow(
//                () -> new NullPointerException("존재하지 않는 사용자 입니다!")
//        );
//        LocalDateTime createdAt = LocalDateTime.now();
//        String formatDate = createdAt.format(DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss", Locale.KOREA));
//        Long enterUserCnt = chatMessageRepository.getUserCnt(messageDto.getRoomId());
//        messageDto.setEnterUserCnt(enterUserCnt);
//        messageDto.setSender(member.getNickname());
//        messageDto.setProfileUrl(member.getProfileImgUrl());
//        messageDto.setCreatedAt(formatDate);
//        messageDto.setMemberId(member.getId());
//        messageDto.setQuitOwner(false);
//
//        //받아온 메세지의 타입이 ENTER 일때
//        if (ChatMessage.MessageType.ENTER.equals(messageDto.getType())) {
//            chatRoomRepository.enterChatRoom(messageDto.getRoomId());
//            messageDto.setMessage( messageDto.getSender() + "님이 입장하셨습니다.");
//            String roomId = messageDto.getRoomId();
//
//
//            List<InvitedUsers> invitedUsersList = invitedUsersRepository.findAllByBoardId(Long.parseLong(roomId));
//            for (InvitedUsers invitedUsers : invitedUsersList) {
//                if (invitedUsers.getMember().equals(member)) {
//                    invitedUsers.setReadCheck(true);
//                }
//            }
//            // 이미 그방에 초대되어 있다면 중복으로 저장을 하지 않게 한다.
//            if (!invitedUsersRepository.existsByMemberIdAndBoardId(member.getId(), Long.parseLong(roomId))) {
//                InvitedUsers invitedUsers = new InvitedUsers(Long.parseLong(roomId), member);
//                invitedUsersRepository.save(invitedUsers);
//            }
//            //받아온 메세지 타입이 QUIT 일때
//        } else if (ChatMessage.MessageType.QUIT.equals(messageDto.getType())) {
//            messageDto.setMessage(messageDto.getSender() + "님이 나가셨습니다.");
//            if (invitedUsersRepository.existsByMemberIdAndBoardId(member.getId(), Long.parseLong(messageDto.getRoomId()))) {
//                invitedUsersRepository.deleteByMemberIdAndBoardId(member.getId(), Long.parseLong(messageDto.getRoomId()));
//            }
//            if (!boardRepository.existsById(Long.parseLong(messageDto.getRoomId()))) {
//                ResignChatRoom chatRoom = resignChatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
//                if (chatRoom.getUsername().equals(member.getUsername())) {
//                    messageDto.setQuitOwner(true);
//                    messageDto.setMessage("(방장) " + messageDto.getSender() + "님이 나가셨습니다. " +
//                            "더 이상 대화를 할 수 없으며 채팅방을 나가면 다시 입장할 수 없습니다.");
//                    boardRepository.deleteById(Long.parseLong(messageDto.getRoomId()));
//                    ChatRoom findChatRoom = chatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
//                    List<ChatMessage> chatMessage = chatMessageJpaRepository.findAllByRoomId(messageDto.getRoomId());
//                    ResignChatRoom resignChatRoom = new ResignChatRoom(findChatRoom);
//                    resignChatRoomJpaRepository.save(resignChatRoom);
//                    for (ChatMessage message : chatMessage) {
//                        ResignChatMessage resignChatMessage = new ResignChatMessage(message);
//                        resignChatMessageJpaRepository.save(resignChatMessage);
//                    }
//                    chatMessageJpaRepository.deleteByRoomId(messageDto.getRoomId());
//                    chatRoomJpaRepository.deleteByRoomId(messageDto.getRoomId());
//                }
//            }else {
//                ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
//                if (chatRoom.getUsername().equals(member.getUsername())) {
//                    messageDto.setQuitOwner(true);
//                    messageDto.setMessage("(방장) " + messageDto.getSender() + "님이 나가셨습니다. " +
//                            "더 이상 대화를 할 수 없으며 채팅방을 나가면 다시 입장할 수 없습니다.");
//                    boardRepository.deleteById(Long.parseLong(messageDto.getRoomId()));
//                    ChatRoom findChatRoom = chatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
//                    List<ChatMessage> chatMessage = chatMessageJpaRepository.findAllByRoomId(messageDto.getRoomId());
//                    ResignChatRoom resignChatRoom = new ResignChatRoom(findChatRoom);
//                    resignChatRoomJpaRepository.save(resignChatRoom);
//                    for (ChatMessage message : chatMessage) {
//                        ResignChatMessage resignChatMessage = new ResignChatMessage(message);
//                        resignChatMessageJpaRepository.save(resignChatMessage);
//                    }
//                    chatMessageJpaRepository.deleteByRoomId(messageDto.getRoomId());
//                    chatRoomJpaRepository.deleteByRoomId(messageDto.getRoomId());
//                }
//            }
//            chatMessageJpaRepository.deleteByRoomId(messageDto.getRoomId());
//        }
//        chatMessageRepository.save(messageDto); // 캐시에 저장 했다.
//        ChatMessage chatMessage = new ChatMessage(messageDto, createdAt);
//        chatMessageJpaRepository.save(chatMessage); // DB 저장
//        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
//        redisPublisher.publish(ChatRoomRepository.getTopic(messageDto.getRoomId()), messageDto);
//    }
//
//    //redis에 저장되어있는 message 들 출력
//    public List<ChatMessageDto> getMessages(String roomId) {
//        return chatMessageRepository.findAllMessage(roomId);
//    }
//
//    //채팅방에 참여한 사용자 정보 조회
//    public List<UserinfoDto> getUserinfo(UserDetailsImpl userDetails, String roomId) {
//        memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
//                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
//        );
//        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByBoardId(Long.parseLong(roomId));
//        List<UserinfoDto> users = new ArrayList<>();
//        for (InvitedUsers invitedUser : invitedUsers) {
//            Member member = invitedUser.getMember();
//            users.add(new UserinfoDto(member.getNickname(), member.getProfileImgUrl(), member.getId()));
//        }
//        return users;
//    }
//
//
//    //유저 정보 상세조회 (채팅방 안에서)
//    public ResponseEntity<UserDetailDto> getUserDetails(String roomId, Long userId) {
//        Member member = memberRepository.findById(userId).orElseThrow(
//                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
//        );
//        ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(roomId);
//
//        if (chatRoom.getUsername().equals(member.getUsername())) {
//            return new ResponseEntity<>(new UserDetailDto(true, "유저 정보 조회 성공", member, true), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new UserDetailDto(true, "유저 정보 조회 성공", member, false), HttpStatus.OK);
//        }
//
//    }
//}
//
