package com.project.uandmeet.controller;

import com.project.uandmeet.dto.MainPageDto;
import com.project.uandmeet.dto.MainPageEntryDto;
import com.project.uandmeet.dto.MainPageMatchingDto;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainPageController {
    private final MainPageService maininformation;
    // mainpage -> information
    @GetMapping("/api/mainboards/information/{category}")
    public ResponseEntity<List<MainPageDto>> maininformation(@PathVariable String category) {
        return ResponseEntity.ok(maininformation.maininformation(category));
    }

    // mainpage -> matching
    @GetMapping("/api/mainboards/matching/{category}")
    public ResponseEntity<List<MainPageMatchingDto>> mainmatching(@PathVariable String category) {
        return ResponseEntity.ok(maininformation.mainmatching(category));
    }

    //     나의 게시글(신청글, 참여글)
    @GetMapping("/api/mainboards/myentry")
    public ResponseEntity<List<MainPageEntryDto>> mainmyentry(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(maininformation.mainmyentry(userDetails));
    }
}
