package com.project.uandmeet.sse;

import com.project.uandmeet.chat.service.CommonUtil;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public SseEmitter subscribe(Long userId, String lastEventId) {
        String id = userId + "_" + System.currentTimeMillis();
        System.out.println("Subscribe");
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() ->{
            System.out.println("Sse onCompletion");
                emitterRepository.deleteById(id);
            }

        );
        emitter.onTimeout(() -> {
                    System.out.println("Sse onTimeout");
                    emitterRepository.deleteById(id);
                }
        );

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void publishSse(Long reciverId, String message){
        //redis message broker로 sse 데이터 전송
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>( NotificationDto.Publish.class));
        redisTemplate.convertAndSend("sse",
                NotificationDto.Publish.builder()
                        .content(message)
                        .reciverId(reciverId)
                        .build());
    }
    @Transactional
    public void save(Member member, String message){

        Notification notification = Notification.builder()
                .content(message)
                .receiver(member)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }
    public void send(NotificationDto.Publish message) {
        Member reciverMember = memberRepository.findById(message.getReciverId()).orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Notification notification = Notification.builder()
                .content(message.getContent())
                .receiver(reciverMember)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        String id = String.valueOf(reciverMember.getId());

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, NotificationDto.Response.builder()
                            .id(notification.getId())
                            .content(notification.getContent())
                            .isRead(notification.getIsRead())
                            .build());
                }
        );
    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
        }
    }
    @Transactional
    public void sseRead(Long notificationId) {
        Member member = CommonUtil.getMember();
        Notification notification = notificationRepository.findByIdAndReceiver(notificationId, member);
        if(notification == null){
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
        notification.updateRead();
    }
    @Transactional
    public void sseDelete(Long notificationId) {
        Member member = CommonUtil.getMember();
        Notification notification = notificationRepository.findByIdAndReceiver(notificationId, member);
        if(notification == null){
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
        notificationRepository.delete(notification);
    }
    public List<NotificationDto.Response> sseGet() {
        Member member = CommonUtil.getMember();
        return notificationRepository.findAllByReceiver(member).stream().map(notification ->
                NotificationDto.Response.builder()
                        .id(notification.getId())
                        .content(notification.getContent())
                        .isRead(notification.getIsRead())
                        .build()
        ).collect(Collectors.toList());
    }
    public boolean sseIsReadGet() {
        Member member = CommonUtil.getMember();
        return notificationRepository.existsByReceiverAndIsRead(member,false);
    }
}