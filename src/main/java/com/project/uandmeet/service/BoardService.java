package com.project.uandmeet.service;

import com.project.uandmeet.Exception.CustomException;
import com.project.uandmeet.Exception.ErrorCode;
import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardResponseDto;
import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Category;
import com.project.uandmeet.model.Like;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.*;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor //생성자 미리 생성.
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepostiory memberRepostiory;
    private final CategoryRepository categoryRepository;

    private final LikeRepository likeRepository;

    //게시판 생성
    @Transactional
    public CustomException boardNew(BoardRequestDto.createAndCheck boardRequestDto, UserDetailsImpl userDetails) {
        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(()->new CustomException(ErrorCode.EMPTY_CONTENT));

        Category category = categoryRepository.findByCategory(boardRequestDto.getCategory())
              .orElseThrow(()->new CustomException(ErrorCode.EMPTY_CONTENT));

        Board board = new Board(memberTemp,category, boardRequestDto);

        try {
            boardRepository.save(board);
            return new CustomException(ErrorCode.COMPLETED_OK);
        }catch (Exception e){
            System.out.println(e);
            return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //게시물 전체 조회
    @Transactional
    public List<BoardResponseDto> boardAllInquiry(String boardType, String categoryName) {

        //개시판 정보 추출
        List<Board> boards = boardRepository.findAllByBoardTypeAndCategory(boardType, categoryName);

        // 찾으 정보를 Dto로 변환 한다.
        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
        if (boards != null) {
            for (Board boardTemp : boards) {
                //작성자 간이 닉네임 생성.
                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boardTemp.getMember().getNickname(),
                        boardTemp.getMember().getEmail(), boardTemp.getMember().getProfile());

                BoardResponseDto boardResponseDto = new BoardResponseDto(memberSimpleDto,boardTemp);
                boardResponseDtos.add(boardResponseDto);
            }
        }
        return boardResponseDtos;
    }

    //게시물 상세 조회
    @Transactional
    public BoardResponseDto boardChoiceInquiry(Long id) {

        //개시판 정보 추출
        Board boards = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        // 찾으 정보를 Dto로 변환 한다.
        BoardResponseDto boardResponseDto = null;

        if (boards != null) {
            //작성자 간이 닉네임 생성.
            MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boards.getMember().getNickname(),
                    boards.getMember().getEmail(), boards.getMember().getProfile());

            boardResponseDto = new BoardResponseDto(memberSimpleDto, boards);
            return boardResponseDto;
        }
        else return null;
    }

    //게시물 삭제.
    @Transactional
    public CustomException boardDel(Long id, UserDetailsImpl userDetails) {

        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //본인이 아니면 예외처리
        if (board.getMember().getEmail().equals(memberTemp.getEmail())) {
            try {
                boardRepository.deleteById(id);
                return new CustomException(ErrorCode.COMPLETED_OK);
            } catch (Exception e) {
                System.out.println(e);
                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            return new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
    }

    //게시물 수정
    @Transactional
    public CustomException boardUpdate(Long id, BoardRequestDto.createAndCheck boardRequestUdateDto,
                                       UserDetailsImpl userDetails) {

        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //본인이 아니면 예외처리
        if (board.getMember().getEmail().equals(memberTemp.getEmail())) {
            Board boardUpdate = new Board(board, boardRequestUdateDto);
            try {
                boardRepository.save(boardUpdate);
                return new CustomException(ErrorCode.COMPLETED_OK);
            } catch (Exception e) {
                System.out.println(e);
                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
    }


    //수정중
    @Transactional
    public CustomException likeClick(LikeDto likeDto, UserDetailsImpl userDetails) {

        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Board board = boardRepository.findById(likeDto.getPostid())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        return null;
    }
}
