package com.project.uandmeet.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    //맨처음 설정값이라고 보면 편함
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("/sub 발동");
        config.enableSimpleBroker("/sub"); // prefix /sub 로 수신 메시지 구분
        //여기로 데이터가 들어온 경우 messageMapping 으로 JUMP
        log.info("/pub 발동");
        config.setApplicationDestinationPrefixes("/pub"); // prefix /pub 로 발행 요청
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
        registry.addEndpoint("/ws-stomp") // url/chatting 웹 소켓 연결 주소
                .setAllowedOriginPatterns("*")
                .withSockJS(); // sock.js를 통하여 낮은 버전의 브라우저에서도 websocket 이 동작할수 있게 한다
    }

    // StompHandler 인터셉터 설정
    // StompHandler 가 Websocket 앞단에서 token 및 메시지 TYPE 등을 체크할 수 있도록 다음과 같이 인터셉터로 설정한다
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(160 * 64 * 1024); // default : 64 * 1024
        registration.setSendTimeLimit(100 * 10000); // default : 10 * 10000 60 * 10000 * 6
        registration.setSendBufferSizeLimit(3* 512 * 1024); // default : 512 * 1024
    }
}

