package com.project.uandmeet.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // emableSimpleBroker spring에서 제공하는 내장브로커 사용
        // 해당 파라미터를 perfix로 받고 있는 경우 그 메세지를 브로커가 처리하겠다
        // "/queue"는 1대1 메세지 , "/topic" 1대 다 로 메세지를 처리할때
        config.enableSimpleBroker("/queue","/alarm");
        // setApplicationDestinationPrefixes
        // 바로 브로커로 가는 경우가 아니라 메세지가 가공이 필요할때 handler로 메세지가
        // 라우팅 되도록 하는 설정
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 인자로 들어가는는 "/ws-stomp"는 첫 websocket 3handskate를 하기 위한 주소
        // cors 설정 과 sockJS설정을 할 수있다.
        // handler를 설정안해도 됨
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")
                .withSockJS();

    }

    // StompHandler가 Websocket 앞단에서 token을 체크할 수 있도록 다음과 같이 인터셉터 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        System.out.println("Stomp핸들러 메시지 가로채기");
        registration.interceptors(stompHandler);
    }
}
