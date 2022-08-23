package com.project.uandmeet.chat.controller;

import com.project.uandmeet.chat.dto.NotificationDto;
import com.project.uandmeet.chat.service.NotificationService;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/notification")
    public List<NotificationDto> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return notificationService.getNotification(userDetails);
    }
}
