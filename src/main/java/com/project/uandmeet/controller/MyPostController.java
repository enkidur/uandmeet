package com.project.uandmeet.controller;

import com.project.uandmeet.dto.MyPostInfoResponseDto;
import com.project.uandmeet.dto.MypostCommentResponseDto;
import com.project.uandmeet.dto.MypostResponseDto;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.MyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MyPostController {
    private final MyPostService myPostService;
    // 나의 게시글 -> 정보게시글
    @GetMapping("/api/mypost/information")
    public ResponseEntity<MyPostInfoResponseDto> mypostinformation(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @RequestParam int page,
                                                                   @RequestParam int amount) {
        page -= 1;
        return ResponseEntity.ok(myPostService.mypostinformation(userDetails, page, amount));
    }

    // 나의 게시글 -> 매칭게시글
    @GetMapping("/api/mypost/matching")
    public ResponseEntity<MypostResponseDto> mypostmatching(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestParam int page,
                                                            @RequestParam int amount) {
        page -= 1;
        return ResponseEntity.ok(myPostService.mypostmatching(userDetails, page, amount));
    }

    // 나의 게시글(내가 신청한 글)
    @GetMapping("/api/myentry")
    public ResponseEntity<MypostResponseDto> myentry(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestParam int page,
                                                     @RequestParam int amount) {
        page -= 1;
        return ResponseEntity.ok(myPostService.myentry(userDetails, page, amount));
    }

    // 나의 댓글 -> information
    @GetMapping("/api/mycomment/information")
    public ResponseEntity<MypostCommentResponseDto> mycommentinformation(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                         @RequestParam int page,
                                                                         @RequestParam int amount) {
        page -= 1;
        return ResponseEntity.ok(myPostService.mycommentinformation(userDetails, page, amount));
    }

    // 나의 댓글 -> matching
    @GetMapping("/api/mycomment/matching")
    public ResponseEntity<MypostCommentResponseDto> mycommentmatching(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @RequestParam int page,
                                                                      @RequestParam int amount) {
        page -= 1;
        return ResponseEntity.ok(myPostService.mycommentmatching(userDetails, page, amount));
    }
}
