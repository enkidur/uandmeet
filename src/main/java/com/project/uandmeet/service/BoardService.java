//package com.project.uandmeet.service;
//
//import com.project.uandmeet.dto.ImageDto;
//import com.project.uandmeet.dto.boardDtoGroup.BoardResponseFinalDto;
//import com.project.uandmeet.dto.commentsDtoGroup.CommentsInquiryDto;
//import com.project.uandmeet.dto.commentsDtoGroup.CommentsReponseDto;
//import com.project.uandmeet.dto.commentsDtoGroup.CommentsRequestDto;
//import com.project.uandmeet.exception.CustomException;
//import com.project.uandmeet.exception.ErrorCode;
//import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
//import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
//import com.project.uandmeet.dto.boardDtoGroup.BoardResponseDto;
//import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
//import com.project.uandmeet.model.*;
//import com.project.uandmeet.repository.*;
//import com.project.uandmeet.security.UserDetailsImpl;
//import com.project.uandmeet.service.S3.S3Uploader;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor //생성자 미리 생성.
//public class BoardService {
//
//    private final BoardRepository boardRepository;
//    private final MemberRepository memberRepostiory;
//    private final CategoryRepository categoryRepository;
//    private final LikedRepository likedRepository;
//    private final EntryRepository entryRepository;
//    private final CommentRepository commentRepository;
//    private final S3Uploader s3Uploader;
//    private final String POST_IMAGE_DIR = "static";
//
//
//
//    //게시판 생성
//    @Transactional
//    public CustomException boardNew(BoardRequestDto.createAndCheck boardRequestDto, UserDetailsImpl userDetails) throws IOException {
//        //로그인 유저 정보.
//        Member memberTemp = memberRepostiory.findByUsername(userDetails.getUsername())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        Category category = categoryRepository.findAllByCategory(boardRequestDto.getCategory())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//
//        if (boardRequestDto.getData() != null) {
//            String uploadImage = String.valueOf(s3Uploader.upload(boardRequestDto.getData(), POST_IMAGE_DIR));
//
//            Board board = new Board(memberTemp, category, boardRequestDto, uploadImage);
//
//            boardRepository.save(board);
//        } else {
//            Board board = new Board(memberTemp, category, boardRequestDto);
//
//            boardRepository.save(board);
//        }
//
//        try {
//            return new CustomException(ErrorCode.COMPLETED_OK);
//        } catch (Exception e) {
//            System.out.println(e);
//            return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    //매칭 게시물 전체 조회 (카테고리별 전체 조회)
//    @Transactional
//    public BoardResponseFinalDto boardMatchingAllInquiry(String type, String cate, Integer page, Integer amount, String city, String gu) {
//        Sort sort = Sort.by("createdAt").descending();
//        PageRequest pageRequest = PageRequest.of(page, amount, sort);
//        Page<Board> boardPage;
//
//        //개시판 정보 추출
//        if (cate.equals("all") || cate.equals("ALL")) {
//            boardPage = boardRepository.findAllByBoardType(type, pageRequest);
//
//        } else
//            boardPage = boardRepository.findAllByBoardTypeAndCategory(type, cate, pageRequest);
//
//        // 찾으 정보를 Dto로 변환 한다.
//        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
//        if (boardPage != null) {
//            for (Board boardTemp : boardPage) {
//                //작성자 간이 닉네임 생성.
//                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boardTemp.getMember().getNickname(),
//                        boardTemp.getMember().getEmail(), boardTemp.getMember().getProfile());
//
//                BoardResponseDto boardResponseDto = new BoardResponseDto(memberSimpleDto, boardTemp);
//                boardResponseDtos.add(boardResponseDto);
//            }
//        }
//        return new BoardResponseFinalDto(boardResponseDtos,boardPage.getTotalElements());
//    }
//
//    //매칭 게시물 상세 조회
//    @Transactional
//    public BoardResponseDto boardChoiceInquiry(Long id) {
//
//        //개시판 정보 추출
//        Board boards = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        // 찾으 정보를 Dto로 변환 한다.
//        BoardResponseDto boardResponseDto = null;
//
//        if (boards != null) {
//            //작성자 간이 닉네임 생성.
//            MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boards.getMember().getNickname(),
//                    boards.getMember().getEmail(), boards.getMember().getProfile());
//
//            boardResponseDto = new BoardResponseDto(memberSimpleDto, boards);
//            return boardResponseDto;
//        } else return null;
//    }
//
//    //게시물 삭제.
//    @Transactional
//    public CustomException boardDel(Long id, UserDetailsImpl userDetails) {
//
//        //로그인 유저 정보.
//        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        //본인이 아니면 예외처리
//        if (board.getMember().getEmail().equals(memberTemp.getEmail())) {
//            try {
//                boardRepository.deleteById(id);
//                return new CustomException(ErrorCode.COMPLETED_OK);
//            } catch (Exception e) {
//                System.out.println(e);
//                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//            }
//        } else {
//            return new CustomException(ErrorCode.INVALID_AUTHORITY);
//        }
//    }
//
//    //매칭 게시물 수정
//    @Transactional
//    public CustomException boardUpdate(Long id, BoardRequestDto.updateMatching boardRequestMatchingUpdateDto,
//                                       UserDetailsImpl userDetails) {
//
//        //로그인 유저 정보.
//        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        //본인이 아니면 예외처리
//        if (board.getMember().getEmail().equals(memberTemp.getEmail())) {
//            Board boardUpdate = new Board(board, boardRequestMatchingUpdateDto);
//            try {
//                boardRepository.save(boardUpdate);
//                return new CustomException(ErrorCode.COMPLETED_OK);
//
//            } catch (Exception e) {
//                System.out.println(e);
//                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//            }
//        } else {
//            return new CustomException(ErrorCode.INVALID_AUTHORITY);
//        }
//    }
//
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
//                if (!likedRepository.findByBoardAndMember(board, memberTemp).isPresent()) {
//
//                    Liked like = new Liked(likeDto, memberTemp, board);
//                    try {
//                        likedRepository.save(like);
//
//                        board.setLikeCount(board.getLikeCount() + 1);
//                        boardRepository.save(board);
//
//                        System.out.println(board);
//
//                        responseEntity = ResponseEntity.ok(board.getLikeCount());
//
//                    } catch (Exception e) {
//                        System.out.println(e);
//                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다.");
//                    }
//                } else {
//                    System.out.println("등록되어 있음");
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 요청사항을 수행할 수 없습니다.");
//                }
//            } else {
//                if (likedRepository.findByBoardAndMember(board, memberTemp).isPresent()) {
//
//                    Liked like = likedRepository.findByBoardAndMember(board, memberTemp)
//                            .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//                    likedRepository.deleteById(like.getId());
//
//                    //게시판에서 좋아요 수 없애기
//                    board.setLikeCount(board.getLikeCount() - 1);
//
//                    boardRepository.save(board);
//
//                    System.out.println(board);
//                    responseEntity = ResponseEntity.ok(board.getLikeCount());
//                } else {
//                    System.out.println("등록 안되어 있음.");
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 요청사항을 수행할 수 없습니다.");
//                }
//            }
//        } else {
//            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
//        }
//
//        return responseEntity;
//    }
//
//
//    //매칭 참여
//    @Transactional
//    public ResponseEntity<Long> matchingJoin(Long id, UserDetailsImpl userDetails) {
//        ResponseEntity<Long> responseEntity = null;
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        Member member = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        if (!entryRepository.findByNicknameAndBoard(member.getNickname(), board).isPresent()) {
//            Entry entry = new Entry(board, member);
//            try {
//                entryRepository.save(entry);
//                board.setLikeCount(board.getLikeCount() + 1);
//
//                boardRepository.save(board);
//                System.out.println(board);
//
//                responseEntity = ResponseEntity.ok(board.getCommentCount());
//
//            } catch (Exception e) {
//                System.out.println(e);
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다.");
//            }
//        }
//
//        return responseEntity;
//    }
//
//    //매칭 참여 취소
//    @Transactional
//    public ResponseEntity<Long> matchingCancel(Long id, UserDetailsImpl userDetails) {
//        ResponseEntity<Long> responseEntity = null;
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        Member member = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        if (entryRepository.findByNicknameAndBoard(member.getNickname(), board).isPresent()) {
//            Entry entry = new Entry(board, member);
//            try {
//                entryRepository.save(entry);
//                board.setLikeCount(board.getLikeCount() - 1);
//
//                boardRepository.save(board);
//                System.out.println(board);
//
//                responseEntity = ResponseEntity.ok(board.getCommentCount());
//
//            } catch (Exception e) {
//                System.out.println(e);
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다.");
//            }
//        }
//
//        return responseEntity;
//    }
//
//
//    //댓글 작성.
//    public CommentsReponseDto commentsNew(Long id, CommentsRequestDto commentsRequestDto, UserDetailsImpl userDetails) {
//        Member member = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        Comment comment = new Comment(commentsRequestDto, member, board);
//        CommentsReponseDto commentsReponseDto = new CommentsReponseDto(comment);
//
//        try {
//            commentRepository.save(comment);
//            return commentsReponseDto;
//
//        } catch (IllegalArgumentException ignored) {
//            System.out.println(ignored);
//            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    //댓글 조회
//    public List<CommentsInquiryDto> commentInquiry(Long id) {
//
//        //Board의 전체 댓글들을 조회한다.
//        List<Comment> commentList = commentRepository.findAllByBoard(id);
//
//        // 찾으 정보를 Dto로 변환 한다.
//        List<CommentsInquiryDto> commentsInquiryDtos = new ArrayList<>();
//
//        if (commentList != null) {
//            for (Comment commentTemp : commentList) {
//                //작성자 간이 닉네임 생성.
//                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(commentTemp.getMember().getNickname(),
//                        commentTemp.getMember().getEmail(), commentTemp.getMember().getProfile());
//
//                CommentsInquiryDto commentsInquiryDto = new CommentsInquiryDto(memberSimpleDto, commentTemp);
//                commentsInquiryDtos.add(commentsInquiryDto);
//            }
//        }
//        return commentsInquiryDtos;
//    }
//
//    //댓글 삭제
//    public CustomException commentDel(Long boardId, Long commentId, UserDetailsImpl userDetails) {
//        //로그인 유저 정보.
//        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//        Comment comment = commentRepository.findByIdAndBoard(commentId, boardId)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        //본인이 아니면 예외처리
//        if (comment.getMember().getEmail().equals(memberTemp.getEmail())) {
//            try {
//                boardRepository.deleteById(boardId);
//                return new CustomException(ErrorCode.COMPLETED_OK);
//            } catch (Exception e) {
//                System.out.println(e);
//                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//            }
//        } else {
//            return new CustomException(ErrorCode.INVALID_AUTHORITY);
//        }
//    }
//
//    public CustomException categoryNew(String category) {
//        Category category1 = new Category(category);
//
//        try {
//            categoryRepository.save(category1);
//            return new CustomException(ErrorCode.COMPLETED_OK);
//        } catch (Exception e) {
//            System.out.println(e);
//            return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    //공유 게시물 전체 조회 (카테고리별 전체 조회)
//    @Transactional
//    public BoardResponseFinalDto boardInfoAllInquiry(String type, String cate, Integer page, Integer amount) {
//        Sort sortInfo = Sort.by("createdAt").descending();
//        PageRequest pageRequest = PageRequest.of(page, amount, sortInfo);
//        Page<Board> boardPage;
//
//        //개시판 정보 추출
//        if (cate.equals("all") || cate.equals("ALL")) {
//            boardPage = boardRepository.findAllByBoardType(type, pageRequest);
//
//        } else
//            boardPage = boardRepository.findAllByBoardTypeAndCategory(type, cate, pageRequest);
//
//        // 찾으 정보를 Dto로 변환 한다.
//        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
//        if (boardPage != null) {
//            for (Board boardTemp : boardPage) {
//                //작성자 간이 닉네임 생성.
//                MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boardTemp.getMember().getNickname(),
//                        boardTemp.getMember().getEmail(), boardTemp.getMember().getProfile());
//
//                BoardResponseDto boardResponseDto = new BoardResponseDto(boardTemp,memberSimpleDto);
//                boardResponseDtos.add(boardResponseDto);
//            }
//        }
//        return new BoardResponseFinalDto(boardResponseDtos,boardPage.getTotalElements());
//    }
//
//    //공유 게시물 상세 조회
//    @Transactional
//    public BoardResponseDto boardChoiceInfoInquiry(Long id) {
//
//        //개시판 정보 추출
//        Board boards = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        // 찾으 정보를 Dto로 변환 한다.
//        BoardResponseDto boardResponseDto = null;
//
//        if (boards != null) {
//            //작성자 간이 닉네임 생성.
//            MemberSimpleDto memberSimpleDto = new MemberSimpleDto(boards.getMember().getNickname(),
//                    boards.getMember().getEmail(), boards.getMember().getProfile());
//
//            boardResponseDto = new BoardResponseDto(boards,memberSimpleDto);
//            return boardResponseDto;
//        } else return null;
//    }
//
//    //공유 게시물 수정
//    @Transactional
//    public CustomException boardInfoUpdate(Long id, BoardRequestDto.updateInfo boardRequestInfoUpdateDto,
//                                       UserDetailsImpl userDetails) {
//
//        //로그인 유저 정보.
//        Member memberTemp = memberRepostiory.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_CONTENT));
//
//        //본인이 아니면 예외처리
//        if (board.getMember().getEmail().equals(memberTemp.getEmail())) {
//            Board boardUpdate = new Board(board, boardRequestInfoUpdateDto);
//            try {
//                boardRepository.save(boardUpdate);
//                return new CustomException(ErrorCode.COMPLETED_OK);
//
//            } catch (Exception e) {
//                System.out.println(e);
//                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//            }
//        } else {
//            return new CustomException(ErrorCode.INVALID_AUTHORITY);
//        }
//    }
//}