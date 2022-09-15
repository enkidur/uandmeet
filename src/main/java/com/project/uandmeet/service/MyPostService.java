package com.project.uandmeet.service;

import com.project.uandmeet.dto.*;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Comment;
import com.project.uandmeet.model.Entry;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.*;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MyPostService {
    private final MemberRepository memberRepository;
    private final EntryRepository entryRepository;

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public MyPostInfoResponseDto mypostinformation (UserDetailsImpl userDetails, int page, int amount){
        // page 함수
        Sort.Direction direction = Sort.Direction.DESC;
        String sortby = "createdAt";
        Sort sort = Sort.by(direction, sortby);
        Pageable pageable = PageRequest.of(page, amount, sort);

        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
//        List<Board> boards = boardRepository.findByMemberAndBoardType(member, "information");
        Page<Board> boards = boardRepository.findByMemberAndBoardType(member, "information", pageable);
        List<MyListInfoResponseDto> boardInfo = new ArrayList<>();
        for (Board board : boards) {
            MyListMemberResponseDto myListMemberResponseDto = new MyListMemberResponseDto(board.getMember().getUsername(),
                    board.getMember().getNickname(),
                    board.getMember().getProfile());
            MyListInfoResponseDto responseDto = new MyListInfoResponseDto(board.getId(),
                    board.getBoardType(),
                    board.getCategory().getCategory(),
                    board.getTitle(),
                    board.getContent(),
                    board.getLikeCount(),
                    board.getViewCount(),
                    board.getCommentCount(),
                    board.getBoardimage(),
                    board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    board.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    myListMemberResponseDto);
            boardInfo.add(responseDto);
        }
        Long informationCount = boardRepository.countByMemberAndAndBoardType(member, "information");
        return new MyPostInfoResponseDto(informationCount, boardInfo);
    }


    public MypostResponseDto mypostmatching (UserDetailsImpl userDetails, int page, int amount){
        // page 함수
        Sort.Direction direction = Sort.Direction.DESC;
        String sortby = "createdAt";
        Sort sort = Sort.by(direction, sortby);
        Pageable pageable = PageRequest.of(page, amount, sort);

        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
//        List<Board> boards = boardRepository.findByMemberAndBoardType(member, "matching");
        Page<Board> boards = boardRepository.findByMemberAndBoardType(member, "matching", pageable);
        List<MyListResponseDto> boardInfo = new ArrayList<>();
        for (Board board : boards) {
            MyListMemberResponseDto myListMemberResponseDto = new MyListMemberResponseDto(board.getMember().getUsername(),
                    board.getMember().getNickname(),
                    board.getMember().getProfile());
            MyListResponseDto responseDto = new MyListResponseDto(board.getId(),
                    board.getBoardType(),
                    board.getCategory().getCategory(),
                    board.getTitle(),
                    board.getContent(),
                    board.getEndDateAt(),
                    board.getLikeCount(),
                    board.getViewCount(),
                    board.getCommentCount(),
                    board.getCity().getCtpKorNmAbbreviation(),
                    board.getGu().getSigKorNm(),
                    board.getLat(),
                    board.getLng(),
                    board.getBoardimage(),
                    board.getMaxEntry(),
                    board.getCurrentEntry(),
                    board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    board.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    myListMemberResponseDto);
            boardInfo.add(responseDto);
        }
        Long matchingCount = boardRepository.countByMemberAndAndBoardType(member, "matching");
        return new MypostResponseDto(matchingCount, boardInfo);
    }

    public MypostResponseDto myentry (UserDetailsImpl userDetails,int page, int amount){
        // page 함수
        Sort.Direction direction = Sort.Direction.DESC;
        String sortby = "createdAt";
        Sort sort = Sort.by(direction, sortby);
        Pageable pageable = PageRequest.of(page, amount, sort);

        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
//        List<Entry> entries = entryRepository.findByMember(member);
        Page<Entry> entries = entryRepository.findByMember(member, pageable);
        List<MyListResponseDto> boardInfo = new ArrayList<>();
        for (Entry entry : entries) {
            MyListMemberResponseDto myListMemberResponseDto = new MyListMemberResponseDto(entry.getBoard().getMember().getUsername(),
                    entry.getBoard().getMember().getNickname(),
                    entry.getBoard().getMember().getProfile());
            MyListResponseDto responseDto = new MyListResponseDto(
                    entry.getBoard().getId(),
                    entry.getBoard().getBoardType(),
                    entry.getBoard().getCategory().getCategory(),
                    entry.getBoard().getTitle(),
                    entry.getBoard().getContent(),
                    entry.getBoard().getEndDateAt(),
                    entry.getBoard().getLikeCount(),
                    entry.getBoard().getViewCount(),
                    entry.getBoard().getCommentCount(),
                    entry.getBoard().getCity().getCtpKorNmAbbreviation(),
                    entry.getBoard().getGu().getSigKorNm(),
                    entry.getBoard().getLat(),
                    entry.getBoard().getLng(),
                    entry.getBoard().getBoardimage(),
                    entry.getBoard().getMaxEntry(),
                    entry.getBoard().getCurrentEntry(),
                    entry.getBoard().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    entry.getBoard().getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    myListMemberResponseDto);
            boardInfo.add(responseDto);
        }
        Long totalCount = entryRepository.countByMember(member);
        return new MypostResponseDto(totalCount, boardInfo);
    }

    public MypostCommentResponseDto mycommentinformation (UserDetailsImpl userDetails,int page, int amount){
        // page 함수
        Sort.Direction direction = Sort.Direction.DESC;
        String sortby = "createdAt";
        Sort sort = Sort.by(direction, sortby);
        Pageable pageable = PageRequest.of(page, amount, sort);

        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        List<MyCommentResponseDto> commentList = new ArrayList<>();
//        List<Comment> comments = commentRepository.findAllByMember(member);
        Page<Comment> comments = commentRepository.findAllByMemberAndBoardType(member, "information", pageable);
        for (Comment comment : comments) {
            MyListMemberResponseDto myListMemberResponseDto = new MyListMemberResponseDto(comment.getMember().getUsername(),
                    comment.getMember().getNickname(),
                    comment.getMember().getProfile());
            MyCommentResponseDto responseDto = new MyCommentResponseDto(
                    comment.getId(),
                    comment.getBoard().getTitle(),
                    comment.getBoard().getId(),
                    comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    comment.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    comment.getComment(),
                    comment.getBoardType(),
                    myListMemberResponseDto);
            commentList.add(responseDto);
        }
        Long informationCount = commentRepository.countByMemberAndBoardType(member, "information");
        return new MypostCommentResponseDto(informationCount, commentList);
    }

    public MypostCommentResponseDto mycommentmatching (UserDetailsImpl userDetails,int page, int amount){
        // page 함수
        Sort.Direction direction = Sort.Direction.DESC;
        String sortby = "createdAt";
        Sort sort = Sort.by(direction, sortby);
        Pageable pageable = PageRequest.of(page, amount, sort);

        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        List<MyCommentResponseDto> commentList = new ArrayList<>();
//        List<Comment> comments = commentRepository.findAllByMember(member);
        Page<Comment> comments = commentRepository.findAllByMemberAndBoardType(member, "matching", pageable);
        for (Comment comment : comments) {
            MyListMemberResponseDto myListMemberResponseDto = new MyListMemberResponseDto(comment.getMember().getUsername(),
                    comment.getMember().getNickname(),
                    comment.getMember().getProfile());
            MyCommentResponseDto responseDto = new MyCommentResponseDto(
                    comment.getId(),
                    comment.getBoard().getTitle(),
                    comment.getBoard().getId(),
                    comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    comment.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    comment.getComment(),
                    comment.getBoardType(),
                    myListMemberResponseDto);
            commentList.add(responseDto);
        }
        Long matchingCount = commentRepository.countByMemberAndBoardType(member, "matching");
        return new MypostCommentResponseDto(matchingCount, commentList);
    }

}
