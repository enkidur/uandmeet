package com.project.uandmeet.controller;


import com.project.uandmeet.dto.CommentRequestDto;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/api/board/{boardid}/comments")
    public void createComments(@PathVariable Long boardid, @RequestBody CommentRequestDto requestsDto,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.createComments(boardid, requestsDto, userDetails);
    }
}
