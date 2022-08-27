package com.project.uandmeet.service;

import com.project.uandmeet.dto.LikeResponseDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardResponseDto;
import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
import com.project.uandmeet.model.*;
import com.project.uandmeet.repository.*;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor //생성자 미리 생성.
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepostiory;
    private final CategoryRepository categoryRepository;
    private final LikedRepository likedRepository;
    private final EntryRepository entryRepository;



    //게시판 생성
    @Transactional
    public CustomException boardNew(BoardRequestDto.createAndCheck boardRequestDto, UserDetailsImpl userDetails) {
        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findByUsername(userDetails.getUsername())
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

        Category category = categoryRepository.findByCategory(categoryName).orElseThrow(()
                        -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //개시판 정보 추출
        List<Board> boards = boardRepository.findAllByBoardTypeAndCategory(boardType, category);

        // 찾으 정보를 Dto로 변환 한다.
        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
        if (boards != null) {
            for (Board boardTemp : boards) {
                //작성자 간이 닉네임 생성.
                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boardTemp.getMember().getNickname(),
                        boardTemp.getMember().getUsername(), boardTemp.getMember().getProfile());

                Board board = boardRepository.findById(boardTemp.getId()).orElseThrow(()
                        -> new CustomException(ErrorCode.EMPTY_CONTENT));

                BoardResponseDto boardResponseDto = new BoardResponseDto(memberSimpleDto,boardTemp);
                if(boardType.equals("information")){  // 정보 게시판일때 좋아요 숫자 카운트해서 보내줌
                    boardResponseDto.setLikeCount(likedRepository.countByBoard(board.getId()));
                    boardResponseDtos.add(boardResponseDto);
                }else{ // 매칭 게시판
                    boardResponseDtos.add(boardResponseDto);
                }
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

    @Transactional
    public Boolean matchinig(UserDetailsImpl userDetails,Long boardId) { // 토큰 , id값 넘겨받음
//        posts.likestate(false);

         // 리턴값 true = 참여된 상태 , false = 참여되지 않은 상태
        boolean exists = entryRepository.existsByMemberAndBoard(userDetails.getMember().getId(),boardId); // postid와 userid가 존재하는 boolean 값
        if (exists) { // 포스트와, 유저 둘다 존재한다면
            entryRepository.deleteByMemberAndBoard(userDetails.getMember().getId(), boardId);
            return true;
        } else { // 존재하지 않는다면 참여
            Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
            Long maxEntry = board.getMaxEntry();  // 최대 인원수

            Long joinCnt = entryRepository.countByBoard(boardId); // 참여자 인원수
            if(joinCnt >= maxEntry){    // 현재 참여한 인원수가 최대 인원수보다 많거나 같으면
                return null;
            }else{
                Member member = memberRepostiory.findById(userDetails.getMember().getId()).orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
                Entry entry = new Entry(board, member, joinCnt);
                entryRepository.save(entry);
                return false;
            }
            }
        }

    @Transactional
    public Boolean information(UserDetailsImpl userDetails, Long boardId) { // 토큰 , id값 넘겨받음

//        posts.likestate(false);

        boolean exists = likedRepository.existsByMemberAndBoard(userDetails.getMember().getId(),boardId); // postid와 userid가 존재하는 boolean 값
        if (exists) { // 포스트와, 유저 둘다 존재한다면
            likedRepository.deleteByMemberAndBoard(userDetails.getMember().getId(), boardId);

            Long likeCnt = likedRepository.countByBoard(boardId); // 좋아요 인원수

            return false;

        } else { // 존재하지 않는다면 참여
            Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

            Long likeCnt = likedRepository.countByBoard(boardId); // 좋아요 인원수

            Member member = memberRepostiory.findById(userDetails.getMember().getId()).orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
            Liked liked = new Liked(board, member);
            likedRepository.save(liked);

            return true;
        }
    }

//
//    //게시물 좋아요 유무
//    @Transactional
//    public ResponseEntity<Long> likeClick(LikeDto likeDto, UserDetailsImpl userDetails) {
//
//        ResponseEntity<Long> responseEntity = null;
//
//        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        Board board = boardRepository.findById(likeDto.getPostid())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        if (board.getMember().getEmail().equals(memberTemp.getEmail())) {
//
//            if (likeDto.getIsLike()) {
//                Liked like = new Liked(likeDto, memberTemp, board);
//                try {
//                    likedRepository.save(like);
//                    Long LikeCountadd = board.getLikeCount() + 1;
//
//                    Board Likeadd = new Board(LikeCountadd, board);
//
//                    boardRepository.save(board);
//                    System.out.println(board);
//
//                    responseEntity = ResponseEntity.ok(board.getLikeCount());
//
//                } catch (Exception e) {
//                    System.out.println(e);
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다.");
//                }
//
//            } else {
//                Liked like = likedRepository.findByBoardAndMember(board, memberTemp)
//                        .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//                likedRepository.deleteById(like.getId());
//            }
//        } else {
//            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
//        }
//
//        return responseEntity;
//    }

/*    public ResponseEntity<Long> matchingJoin(Long id, UserDetailsImpl userDetails) {
        ResponseEntity<Long> responseEntity = null;

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Member member = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        if(!entryRepository.findByNicknameAndBoard(member.getNickname(), board).isPresent()) {
            Entry entry = new Entry(board, member);
        }

            return responseEntity;
    }*/
}
