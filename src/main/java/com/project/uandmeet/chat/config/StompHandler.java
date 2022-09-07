package com.project.uandmeet.chat.config;

import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.chat.service.ChatRoomService;
import com.project.uandmeet.chat.service.ChatService;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.jwt.JwtAuthorizationFilter;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

/**
 * <h1>StompHandler</h1>
 * <p>컨트롤러에 가기전 인터셉터를 통해 먼저 경유하게 된다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;
    private final ChatService chatMessageService;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT: {}", jwtToken);
            jwtTokenProvider.validateToken(jwtToken);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = chatMessageService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            String jwtToken = accessor.getFirstNativeHeader("Authorization");

            Member member;
            if (jwtToken != null) {
                //토큰으로 user 가져옴
                member = memberRepository.findByNickname(jwtTokenProvider.decodeUsername(jwtToken), Member.class)
                        .orElseThrow(()->new IllegalArgumentException("member 가 존재하지 않습니다."));
            }else {
                throw new IllegalArgumentException("유효하지 않은 token 입니다.");
            }

            Long memberId = member.getId();

            chatRoomService.setMemberEnterInfo(memberId, roomId);

            // 채팅방의 인원수를 +1한다.
            chatRoomService.plusMemberCount(roomId);

            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            chatMessageService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(member.getNickname())
                    .build());

            log.info("SUBSCRIBED {}, {}", member.getNickname(), roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomService.getMemberEnterRoomId(sessionId);

            // 채팅방의 인원수를 -1한다.
            chatRoomService.minusMemberCount(roomId);

            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatMessageService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomService.removeMemberEnterInfo(sessionId);

            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
}