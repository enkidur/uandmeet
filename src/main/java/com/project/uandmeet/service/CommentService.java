package com.project.uandmeet.service;

import com.project.uandmeet.dto.CommentRequestDto;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Comment;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.repository.CommentRepository;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    @Transactional
    public void createComments(Long postid, CommentRequestDto requestsDto, UserDetailsImpl userDetails) {
        Board boardid = boardRepository.findById(postid).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다.")
        );
        Member memberid = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                ()-> new IllegalArgumentException("없는 사용자입니다.")
        );
        // 요청 받은 dto 로  db 에 객체 만듦
        Comment comment = new Comment(requestsDto, memberid, boardid);
        System.out.println(comment);
        commentRepository.save(comment);
    }
}
