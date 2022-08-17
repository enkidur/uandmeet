package com.project.uandmeet.service;

import com.project.uandmeet.dto.LikeDto;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Like;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.repository.LIkeRepository;
import com.project.uandmeet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final LIkeRepository lIkeRepository;
    private final BoardService boardService;

    public void likes(LikeDto likeDto) {
        Member memberid = memberRepository.findById(likeDto.getUserid()).orElseThrow(
                ()-> new IllegalArgumentException("없는 사용자입니다.")
        );
        Board boardid = boardRepository.findById(likeDto.getPostid()).orElseThrow(
                ()-> new IllegalArgumentException("없는 게시글입니다.")
        );

        Like like = new Like(memberid, boardid);
        boolean exists = lIkeRepository.existsByBoardAndMember(likeDto.getPostid(), likeDto.getUserid());
        if (exists) {
            boardService.minuslikecnt(likeDto.getPostid()); // commentcount 처럼 size나 count 를 쓴다면 필요없음
            lIkeRepository.deleteByBoardAndMember(likeDto.getPostid(), likeDto.getUserid());
        } else {
            boardService.pluslikecnt(likeDto.getPostid());
            lIkeRepository.save(like);

        }
    }
}
