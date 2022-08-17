package com.project.uandmeet.controller;

import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.dto.LikeDto;
import com.project.uandmeet.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/board/likes/{postid}")
    public void like(@PathVariable Long postid, @AuthenticationPrincipal UserDetailsImpl userDetails){
        if (userDetails != null) {
            LikeDto likeDto = new LikeDto(postid, userDetails.getMember().getId());
            likeService.likes(likeDto);
        }
    }
}
