package com.project.uandmeet.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 로그인 한 유저 sse 연결
     */
    // @GetMapping(value = "/subscribe/{id}", produces = "text/event-stream")
    // public SseEmitter subscribe(@PathVariable Long id,
    //                             @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
    //     return notificationService.subscribe(id, lastEventId);
    // }

    @PutMapping(value = "/api/sse/{notificationId}")
    public ResponseDto<?> sseRead(@PathVariable Long notificationId){
        notificationService.sseRead(notificationId);
        return ResponseDto.builder()
                .ok(true)
                .message("요청 성공")
                .build();
    }

    @DeleteMapping(value = "/api/sse/{notificationId}")
    public ResponseDto<?> sseDelete(@PathVariable Long notificationId){
        notificationService.sseDelete(notificationId);
        return ResponseDto.builder()
                .ok(true)
                .message("삭제 성공")
                .build();
    }
    @GetMapping(value = "/api/sse")
    public ResponseDto<?> sseGet(){
        return ResponseDto.builder()
                .ok(true)
                .message("요청 성공")
                .result(notificationService.sseGet())
                .build();
    }
    @GetMapping(value = "/api/readsse")
    public ResponseDto<?> sseReadGet(){
        return ResponseDto.builder()
                .ok(true)
                .message("요청 성공")
                .result(notificationService.sseIsReadGet())
                .build();
    }


}