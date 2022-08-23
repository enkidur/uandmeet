package com.project.uandmeet.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 @EnableWebSocketMessageBroker : 메시지 브로커가 메시지를 처리할 수 있게 활성화시킨다.
 @configureMessageBroker : enableSimpleBroker를 통해 메시지 브로커가 /topic/chat으로 시작하는 주소를 구독한 Subscriber들에게 메시지를 전달하도록 한다. setApplicationDestinationPrefixes는 클라이언트가 서버로 메시지를 발송할 수 있는 경로의 prefix를 지정한다.
 @registerStompEndpoints : 소켓에 연결하기 위한 엔드 포인트를 지정해준다. 이 때 CORS를 피하기 위해 AllowedOriginPatterns를 *으로 지정해줬다.(실무에서는 정확한 도메인 지정 필요)
 @configureClientInboundChannel: jwt 토큰 검증을 위해 생성한 stompHandler를 인터셉터로 지정해준다.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler; // jwt 인증

    /**
     * 메세지브로커 설정
     * 내부 브로커 사용 및 "/queue", "/topic" 캐치
     * prefix : "/api"
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue");
        registry.setApplicationDestinationPrefixes("/api");
    }

    /**
     * 소켓 관련 설정 메소드
     * endpoint 설정(, 예외 핸들러 설정)
     * @param registry
     * */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 메세지 관련 사용자 검증 설정 메소드
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
