package com.project.uandmeet.service;

import com.project.uandmeet.dto.ImageDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardResponseFinalDto;
import com.project.uandmeet.dto.commentsDtoGroup.CommentsInquiryDto;
import com.project.uandmeet.dto.commentsDtoGroup.CommentsRequestDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import com.project.uandmeet.dto.boardDtoGroup.BoardResponseDto;
import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
import com.project.uandmeet.model.*;
import com.project.uandmeet.repository.*;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
    private final CommentRepository commentRepository;
    private final S3Uploader s3Uploader;
    private final String POST_IMAGE_DIR = "static";

    private final SiareaRepostiory siareaRepostiory;
    private final GuareaRepostiory guareaRepostiory;


    //게시판 생성
    @Transactional
    public ResponseEntity<Long> boardNew(BoardRequestDto.createAndCheck boardRequestDto, UserDetailsImpl userDetails) throws IOException, CustomException {
        ResponseEntity<Long> responseEntity;
        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Category category = categoryRepository.findAllByCategory(boardRequestDto.getCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Siarea siarea = null;
        Guarea guarea = null;

        if (boardRequestDto.getBoardType().equals("matching")) {
            siarea = siareaRepostiory.findByCtpKorNmAbbreviation(boardRequestDto.getCity())
                    .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

            try {
                guarea = guareaRepostiory.findAllBySiareaAndSigKorNm(siarea, boardRequestDto.getGu())
                        .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
            } catch (Exception e) {
                throw new CustomException(ErrorCode.EMPTY_CONTENT);
            }
        }

        if (boardRequestDto.getData() != null) {
            ImageDto uploadImage = s3Uploader.upload(boardRequestDto.getData(), POST_IMAGE_DIR);

            try {
                Board board = new Board(memberTemp, category, siarea, guarea, boardRequestDto, uploadImage.getImageUrl());
                boardRepository.save(board);
                responseEntity = ResponseEntity.ok(board.getId());
            } catch (Exception e) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            Board board = new Board(memberTemp, category, siarea, guarea, boardRequestDto);

            try {
                boardRepository.save(board);
                responseEntity = ResponseEntity.ok(board.getId());

            } catch (Exception e) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return responseEntity;
    }

    //매칭 게시물 전체 조회 (카테고리별 전체 조회)
    @Transactional
    public BoardResponseFinalDto boardMatchingAllInquiry(String type, String cate, Integer page, Integer amount, String city, String gu) {

        //페이지 번호 변경
        if (page > 0)
            page = page - 1;
        else if (page <= 0)
            page = 0;

        Sort sort = Sort.by("createdAt").ascending();
        PageRequest pageRequest = PageRequest.of(page, amount, sort);
        Page<Board> boardPage;
        Category category = null;
        Siarea siarea = null;
        Guarea guarea = null;


        //들어온 문자열 제차 확인
        if (!(cate.equals("all") || cate.equals("ALL"))) {

            category = categoryRepository.findAllByCategory(cate)
                    .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        }

        if (!(city.equals("all") || city.equals("ALL"))) {
            siarea = siareaRepostiory.findByCtpKorNmAbbreviation(city)
                    .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        }

        if (!(gu.equals("all") || gu.equals("All"))) {
            guarea = guareaRepostiory.findAllBySiareaAndSigKorNm(siarea, gu)
                    .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        }

        //개시판 정보 추출
        if (cate.equals("all") || cate.equals("ALL")) {
            if (city.equals("all") || city.equals("ALL"))
                boardPage = boardRepository.findAllByBoardType(type, pageRequest);
            else {
                if (gu.equals("all") || gu.equals("All"))
                    boardPage = boardRepository.findAllByBoardTypeAndCity(type, pageRequest, siarea);
                else
                    boardPage = boardRepository.findAllByBoardTypeAndCityAndGu(type, pageRequest, siarea, guarea);
            }
        } else {
            if (city.equals("all") || city.equals("ALL"))
                boardPage = boardRepository.findAllByBoardTypeAndCategory(type, category, pageRequest);
            else {
                if (gu.equals("all") || gu.equals("All"))
                    boardPage = boardRepository.findAllByBoardTypeAndCategoryAndCity(type, category, siarea, pageRequest);
                else
                    boardPage = boardRepository.findAllByBoardTypeAndCategoryAndCityAndGu(type, category, pageRequest, siarea, guarea);
            }
        }

        // 찾으 정보를 Dto로 변환 한다.
        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
        if (boardPage != null) {
            for (Board boardTemp : boardPage) {
                //작성자 간이 닉네임 생성.
                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boardTemp.getMember().getNickname(),
                        boardTemp.getMember().getUsername(), boardTemp.getMember().getProfile());

                BoardResponseDto boardResponseDto = new BoardResponseDto(memberSimpleDto, boardTemp);
                boardResponseDtos.add(boardResponseDto);
            }
        }
        return new BoardResponseFinalDto(boardResponseDtos, boardPage.getTotalElements());
    }

    //매칭 게시물 상세 조회
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
                    boards.getMember().getUsername(), boards.getMember().getProfile());

            boardResponseDto = new BoardResponseDto(memberSimpleDto, boards);
            return boardResponseDto;
        } else return null;
    }

    //매칭 게시물 상세 조회 (로그인 후 )
    @Transactional
    public BoardResponseDto boardChoiceLoginInquiry(Long id, UserDetailsImpl userDetails) {

        Member member = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //개시판 정보 추출
        Board boards = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Liked liked = likedRepository.findByBoardAndMember(boards, member)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        // 찾으 정보를 Dto로 변환 한다.
        BoardResponseDto boardResponseDto = null;

        if (boards != null) {
            //작성자 간이 닉네임 생성.
            MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boards.getMember().getNickname(),
                    boards.getMember().getUsername(), boards.getMember().getProfile());

            boardResponseDto = new BoardResponseDto(memberSimpleDto, boards,liked);
            return boardResponseDto;

        } else return null;
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
        if (board.getMember().getUsername().equals(memberTemp.getUsername())) {
            try {
                boardRepository.deleteById(id);
                return new CustomException(ErrorCode.COMPLETED_OK);
            } catch (Exception e) {
                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
    }

    //매칭 게시물 수정
    @Transactional
    public CustomException boardUpdate(Long id, BoardRequestDto.updateMatching boardRequestMatchingUpdateDto,
                                       UserDetailsImpl userDetails) {

        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //본인이 아니면 예외처리
        if (board.getMember().getUsername().equals(memberTemp.getUsername())) {
            Board boardUpdate = new Board(board, boardRequestMatchingUpdateDto);
            try {
                boardRepository.save(boardUpdate);
                return new CustomException(ErrorCode.COMPLETED_OK);

            } catch (Exception e) {
                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
    }


    //게시물 좋아요 유무
    @Transactional
    public ResponseEntity<Long> likeClick(LikeDto likeDto, UserDetailsImpl userDetails) {

        ResponseEntity<Long> responseEntity = null;

        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Board board = boardRepository.findById(likeDto.getBoardid())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        if (board.getMember().getId().equals(memberTemp.getId())) {
            if (likeDto.getIsLike()) {
                if (!likedRepository.findByBoardAndMember(board, memberTemp).isPresent()) {

                    Liked like = new Liked(likeDto, memberTemp, board);
                    try {
                        likedRepository.save(like);

                        board.setLikeCount(board.getLikeCount() + 1);
                        boardRepository.save(board);

                        responseEntity = ResponseEntity.ok(board.getLikeCount());

                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다.");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 요청사항을 수행할 수 없습니다.");
                }
            } else {
                if (likedRepository.findByBoardAndMember(board, memberTemp).isPresent()) {

                    Liked like = likedRepository.findByBoardAndMember(board, memberTemp)
                            .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

                    likedRepository.deleteById(like.getId());

                    //게시판에서 좋아요 수 없애기
                    board.setLikeCount(board.getLikeCount() - 1);

                    boardRepository.save(board);

                    responseEntity = ResponseEntity.ok(board.getLikeCount());
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 요청사항을 수행할 수 없습니다.");
                }
            }
        } else {
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
        }

        return responseEntity;
    }


    //매칭 참여
    @Transactional
    public ResponseEntity<Long> matchingJoin(Long id, UserDetailsImpl userDetails) {
        ResponseEntity<Long> responseEntity = null;

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Member member = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        if (!entryRepository.findByMemberAndBoard(member, board).isPresent()) {
            Entry entry = new Entry(board, member);
            try {
                entryRepository.save(entry);
                board.setCurrentEntry(board.getCurrentEntry() + 1);

                boardRepository.save(board);

                responseEntity = ResponseEntity.ok(board.getCurrentEntry());

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다.");
            }
        } else
            ResponseEntity.status(HttpStatus.valueOf("이미 참여 했습니다."));

        return responseEntity;
    }

    //매칭 참여 취소
    @Transactional
    public ResponseEntity<Long> matchingCancel(Long id, UserDetailsImpl userDetails) {
        ResponseEntity<Long> responseEntity = null;

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Member member = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Entry entry = entryRepository.findByMemberAndBoard(member, board)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        if (entry != null) {
            try {
                entryRepository.delete(entry);
                board.setCurrentEntry(board.getCurrentEntry() - 1);

                boardRepository.save(board);

                responseEntity = ResponseEntity.ok(board.getCurrentEntry());

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다.");
            }
        }

        return responseEntity;
    }


    //댓글 작성.
    @Transactional
    public ResponseEntity<CommentsInquiryDto> commentsNew(Long id, CommentsRequestDto commentsRequestDto, UserDetailsImpl userDetails) {
        Member member = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));


        MemberSimpleDto memberSimpleDto = new MemberSimpleDto(member.getNickname(),
                member.getUsername(), member.getProfile());


        Comment comment = new Comment(commentsRequestDto, member, board);

        try {
            commentRepository.save(comment);

        } catch (IllegalArgumentException ignored) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        CommentsInquiryDto commentsInquiryDto = new CommentsInquiryDto(memberSimpleDto, comment);

        return ResponseEntity.ok(commentsInquiryDto);
    }

    //댓글 조회
    public List<CommentsInquiryDto> commentInquiry(Long id) {

        //Board의 전체 댓글들을 조회한다.
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        List<Comment> commentList = commentRepository.findByBoardOrderByCreatedAtDesc(board);

        // 찾으 정보를 Dto로 변환 한다.
        List<CommentsInquiryDto> commentsInquiryDtos = new ArrayList<>();

        if (commentList != null) {
            for (Comment commentTemp : commentList) {
                //작성자 간이 닉네임 생성.
                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(commentTemp.getMember().getNickname(),
                        commentTemp.getMember().getUsername(), commentTemp.getMember().getProfile());

                CommentsInquiryDto commentsInquiryDto = new CommentsInquiryDto(memberSimpleDto, commentTemp);
                commentsInquiryDtos.add(commentsInquiryDto);
            }
        }
        return commentsInquiryDtos;
    }

    //댓글 삭제
    @Transactional
    public CustomException commentDel(Long boardId, Long commentId, UserDetailsImpl userDetails) {
        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        Comment comment = commentRepository.findByIdAndBoard(commentId, board)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //본인이 아니면 예외처리
        if (comment.getMember().getUsername().equals(memberTemp.getUsername())) {
            try {
                commentRepository.deleteById(comment.getId());

                return new CustomException(ErrorCode.COMPLETED_OK);
            } catch (Exception e) {
                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
    }

    public CustomException categoryNew(String category) {

        String[] TEMP = new String[]{"gym", "running", "ridding", "badminton", "tennis", "golf", "hiking", "ballet", "climing", "pilates", "swiming", "boxing", "bowling",
                "crossfit", "gymnastics", "skateboard", "skate", "pocketball", "ski", "futsal", "pingpong", "basketball", "baseball", "soccer", "volleyball", "etc"};
        try {
            for (String s : TEMP) {
                Category category1 = new Category(s);
                categoryRepository.save(category1);
            }

            return new CustomException(ErrorCode.COMPLETED_OK);
        } catch (Exception e) {
            return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //공유 게시물 전체 조회 (카테고리별 전체 조회)
    @Transactional
    public BoardResponseFinalDto boardInfoAllInquiry(String type, String cate, Integer page, Integer amount) {

        if (page > 0)
            page = page - 1;
        else if (page <= 0)
            page = 0;

        Sort sortInfo = Sort.by("createdAt").ascending();
        PageRequest pageRequest = PageRequest.of(page, amount, sortInfo);
        Page<Board> boardPage;
        Category category = null;
        //페이지 번호 변경

        //들어온 문자열 제차 확인
        if (!(cate.equals("all") || cate.equals("ALL"))) {
            category = categoryRepository.findAllByCategory(cate)
                    .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        }

        //개시판 정보 추출
        if (cate.equals("all") || cate.equals("ALL")) {
            boardPage = boardRepository.findAllByBoardType(type, pageRequest);
        } else
            boardPage = boardRepository.findAllByBoardTypeAndCategory(type, category, pageRequest);

        // 찾으 정보를 Dto로 변환 한다.
        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();

        if (boardPage != null) {
            for (Board boardTemp : boardPage) {
                //작성자 간이 닉네임 생성.
                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boardTemp.getMember().getNickname(),
                        boardTemp.getMember().getUsername(), boardTemp.getMember().getProfile());

                BoardResponseDto boardResponseDto = new BoardResponseDto(boardTemp, memberSimpleDto);
                boardResponseDtos.add(boardResponseDto);
            }
        }
        return new BoardResponseFinalDto(boardResponseDtos, boardPage != null ? boardPage.getTotalElements() : 0);
    }

    //공유 게시물 상세 조회
    @Transactional
    public BoardResponseDto boardChoiceInfoInquiry(Long id) {

        //개시판 정보 추출
        Board boards = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        // 찾으 정보를 Dto로 변환 한다.
        BoardResponseDto boardResponseDto = null;

        if (boards != null) {
            //작성자 간이 닉네임 생성.
            MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boards.getMember().getNickname(),
                    boards.getMember().getUsername(), boards.getMember().getProfile());

            boardResponseDto = new BoardResponseDto(boards, memberSimpleDto);
            return boardResponseDto;
        } else return null;
    }

    //공유 게시물 상세 조회 (로그인 후)
    @Transactional
    public BoardResponseDto boardChoiceInfoLoginInquiry(Long id, UserDetailsImpl userDetails) {

        //로그인 유저 정보.
        Member member = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //개시판 정보 추출
        Board boards = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Liked liked = likedRepository.findByBoardAndMember(boards, member)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        // 찾으 정보를 Dto로 변환 한다.
        BoardResponseDto boardResponseDto = null;

        if (boards != null) {
            //작성자 간이 닉네임 생성.
            MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boards.getMember().getNickname(),
                    boards.getMember().getUsername(), boards.getMember().getProfile());


            boardResponseDto = new BoardResponseDto(memberSimpleDto, boards, liked);
            return boardResponseDto;
        } else return null;
    }

    //공유 게시물 수정
    @Transactional
    public CustomException boardInfoUpdate(Long id, BoardRequestDto.updateInfo boardRequestInfoUpdateDto,
                                           UserDetailsImpl userDetails) {

        //로그인 유저 정보.
        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));

        //본인이 아니면 예외처리
        if (board.getMember().getUsername().equals(memberTemp.getUsername())) {
            Board boardUpdate = new Board(board, boardRequestInfoUpdateDto);
            try {
                boardRepository.save(boardUpdate);
                return new CustomException(ErrorCode.COMPLETED_OK);

            } catch (Exception e) {
                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new CustomException(ErrorCode.INVALID_AUTHORITY);
        }
    }

/*
    @Transactional
    public BoardResponseFinalDto boardMatchingmypageAllInquiry(String type,
                                                               String cate,
                                                               Integer page,
                                                               Integer amount,
                                                               UserDetailsImpl userDetails) {
        if (page > 0)
            page = page - 1;
        else if (page <= 0)
            page = 0;

        Sort sortInfo = Sort.by("createdAt").ascending();
        PageRequest pageRequest = PageRequest.of(page, amount, sortInfo);
        Page<Board> boardPage;
        Category category = null;
        //페이지 번호 변경

        //들어온 문자열 제차 확인
        if (!(cate.equals("all") || cate.equals("ALL"))) {
            category = categoryRepository.findAllByCategory(cate)
                    .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
        }

        //개시판 정보 추출 (본인이 쓴것만)
        if (cate.equals("all") || cate.equals("ALL")) {
            boardPage = boardRepository.findAllByBoardTypeAndMember("matching", pageRequest);
        } else
            boardPage = boardRepository.findAllByBoardTypeAndCategory("matching", category, pageRequest);

        // 찾으 정보를 Dto로 변환 한다.
        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();

        if (boardPage != null) {
            for (Board boardTemp : boardPage) {
                //작성자 간이 닉네임 생성.
                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boardTemp.getMember().getNickname(),
                        boardTemp.getMember().getUsername(), boardTemp.getMember().getProfile());

                BoardResponseDto boardResponseDto = new BoardResponseDto(boardTemp, memberSimpleDto);
                boardResponseDtos.add(boardResponseDto);
            }
        }
        return new BoardResponseFinalDto(boardResponseDtos, boardPage != null ? boardPage.getTotalElements() : 0);
    }


*/

}