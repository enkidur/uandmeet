package com.project.uandmeet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeet.dto.*;
import com.project.uandmeet.dto.boardDtoGroup.LikeDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.*;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.*;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.security.jwt.JwtProperties;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import com.project.uandmeet.service.S3.S3Uploader;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final EntryRepository entryRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikedRepository likedRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private int emailCnt = 0; // email 인증 횟수

    // 난수 생성
    public String makeRandomNumber() {
        String checkNum = UUID.randomUUID().toString().substring(0, 6);
        System.out.println("임시 비밀번호 : " + checkNum);
        return checkNum;
    }

    // 회원 가입 1. emali check
    public String checkemail(String username) {
        checkEmail(username);

        // email 중복 확인
        checkDuplicateEmail(username);
        return "email check";
    }

    private void checkEmail(String username) {
        String[] emailadress = username.split("@");
        String id = emailadress[0];
        String host = emailadress[1];
        String pattern = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+.[a-zA-Z0-9.-]*$";
        String idpattern = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]*$";
        String hostpattern = "^[a-zA-Z0-9.-]*$";
        // email 조건
        // ID 영문 대소문자, 숫자, _!#$%&'\*+/=?{|}~^.- 특문허용
        // Host 시작전 @, 영문 대소문자, 숫자, .-특문허용

        // 회원가입 username 조건
        if (username.length() < 10) {
            throw new IllegalArgumentException("이메일을 10자 이상 입력하세요");
        } else if (!Pattern.matches(idpattern, id)) {
            throw new IllegalArgumentException("id에 알파벳 대소문자와 숫자, 특수기호( _!#$%&'\\*+/=?{|}~^.-)로만 입력하세요");
        } else if (!Pattern.matches(hostpattern, host)) {
            throw new IllegalArgumentException("host에 알파벳 대소문자와 숫자, 특수기호(.-)로만 입력하세요");
        } else if (!Pattern.matches(pattern, username)) {
            throw new IllegalArgumentException("이메일 규격에 맞게 입력하세요");
        } else if (username.contains("script")) {
            throw new IllegalArgumentException("xss공격 멈춰주세요.");
        }
    }


    // 비밀번호, 비밀번호 재입력 확인
    public String checkPassword(String password, String passwordCheck) {
        if (password.length() < 3) {
            throw new IllegalArgumentException("비밀번호를 3자 이상 입력하세요");
        } else if (password.length() > 21) {
            throw new IllegalArgumentException("비밀번호를 20자 이하로 입력하세요");
        } else if (password.contains("script")) {
            throw new IllegalArgumentException("xss공격 멈춰주세요.");
        } else if (passwordCheck.contains("script")) {
            throw new IllegalArgumentException("xss공격 멈춰주세요.");
        }
        if (!(passwordCheck.equals(password))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return "password check 완료";
    }

    @Transactional
    public String signup(MemberRequestDto requestDto) {
        String username = requestDto.getUsername();
        String[] emailadress = username.split("@");
        String id = emailadress[0];
        String uuid = UUID.randomUUID().toString().substring(0, 5);
        String uniqueId = id + uuid;
        // 이메일 패턴 체크
        checkEmail(username);
        // 이메일 중복 체크
        checkDuplicateEmail(username);
        checkPassword(requestDto.getPassword(), requestDto.getPasswordCheck());
        Member member = requestDto.register();
        member.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        // login 구별
        member.setLoginto("normal");
        // 초기 닉네임
        member.setNickname(uniqueId);

        // 프로필 이미지 추가
//        if (requestDto.getUserProfileImage() != null) {
//            String profileUrl = s3Uploader.upload(requestDto.getUserProfileImage(), "profile");
//            users.setUserProfileImage(profileUrl);
//        }

        memberRepository.save(member);
        return "회원가입 완료";
    }


    public void checkDuplicateEmail(String username) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }


    // 회원 탈퇴
    @Transactional
    public ResponseEntity<String> withdraw(UserDetailsImpl userDetails, String password) {
        ResponseEntity<String> responseEntity = null;

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            String username = userDetails.getUsername();

            List<Entry> entries = entryRepository.findByMember(userDetails.getMember());
            List<Liked> likeds = likedRepository.findByMember(userDetails.getMember());
            List<Comment> commentList = commentRepository.findByMember(userDetails.getMember());

            //매칭 참여 했던 것들 지우기.
            for (Entry entry : entries) {
                Board board = boardRepository.findById(entry.getBoard().getId())
                        .orElseGet(() -> null);

                if (board != null) {
                    board.setCurrentEntry(board.getCurrentEntry() - 1);
                    boardRepository.save(board);
                }
                entryRepository.delete(entry);
            }

            //좋아요 참여 했던 것들 지우기.
            for (Liked liked : likeds) {
                Board board = boardRepository.findById(liked.getBoard().getId())
                        .orElseGet(() -> null);

                if (board != null) {
                    board.setLikeCount(board.getLikeCount() - 1);
                    boardRepository.save(board);
                }

                likedRepository.delete(liked);
            }

            //댓글 지우기
            for (Comment comment : commentList) {
                Board board = boardRepository.findById(comment.getBoard().getId())
                        .orElseGet(() -> null);

                if (board != null) {
                    board.setCommentCount(board.getCommentCount() - 1);
                    boardRepository.save(board);
                }

                commentRepository.delete(comment);
            }

            try {
                memberRepository.deleteByUsername(username);
                responseEntity = ResponseEntity.ok("회원 탈퇴 완료.");
            }catch (Exception e)
            {
                System.out.println(e);
                responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.valueOf(e));
            }
        }
        else
            responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 비밀번호입니다.");

        return responseEntity;
    }


    @Transactional
    public String refresh(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        //AccessToken
        String expiredAccessTokenHeader = request.getHeader(JwtProperties.HEADER_ACCESS);
//        String expiredAccessToken = jwtTokenProvider.setTokenName(expiredAccessTokenHeader); // barrer 제거
        if (expiredAccessTokenHeader == null || !expiredAccessTokenHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new CustomException(ErrorCode.EMPTY_CONTENT);
        }
        String expiredAccessTokenName = jwtTokenProvider.getExpiredAccessTokenPk(expiredAccessTokenHeader);
        // refreshToken
        String authorizationHeader = redisUtil.getData(expiredAccessTokenName + JwtProperties.HEADER_REFRESH);

        // 최신 토큰인지 검사
        if (!redisUtil.getData(expiredAccessTokenName + JwtProperties.HEADER_ACCESS).equals(expiredAccessTokenHeader)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Refresh Token 유효성 검사
        jwtTokenProvider.validateToken(authorizationHeader);
        String username = jwtTokenProvider.getUserPk(authorizationHeader);
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        Long userId = member.getId();
        // Access Token 재발급
        String accessToken = jwtTokenProvider.createToken(username, userId);

//        Map<String, String> accessTokenResponseMap = new HashMap<>();

        // 현재시간과 Refresh Token 만료날짜를 통해 남은 만료기간 계산 (만료기간 전 재발급이 필요없다면 삭제)
        // Refresh Token 만료시간 계산해 특정 시간 미만일 시 refresh token 도 재발급
        Date now = new Date();
        Date refreshExpireTime = jwtTokenProvider.ExpireTime(authorizationHeader);
        if (refreshExpireTime.before(new Date(now.getTime() + 1000 * 60 * 60 * 24L))) { // refresh token 만료시간이 특정시간보다 작으면 재발급
            String newRefreshToken = jwtTokenProvider.createRefreshToken(username);
//            accessTokenResponseMap.put(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + newRefreshToken);
            redisUtil.setDataExpire(jwtTokenProvider.getUserPk(accessToken) + JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken, JwtProperties.ACCESS_EXPIRATION_TIME);
            redisUtil.setDataExpire(jwtTokenProvider.getUserPk(accessToken) + JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + newRefreshToken, JwtProperties.REFRESH_EXPIRATION_TIME);
        }

        response.setHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        return "재발급 성공";
    }

    @Transactional
    public String findpassword(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        redisUtil.setDataExpire("passAuth" + username, makeRandomNumber(), 60 * 3L);
        redisUtil.setDataExpire("passCnt" + username, String.valueOf(emailCnt), 60 * 60L);
        if (Integer.parseInt(redisUtil.getData("Cnt" + username)) < 4) {
            redisUtil.setDataExpire("Cnt" + username, String.valueOf(emailCnt + 1), 60 * 60L);
            int restCnt = 3 - Integer.parseInt(redisUtil.getData("Cnt" + username));


            //인증메일 보내기
            String setFrom = "wjdgns5488@naver.com"; // email-config에 설정한 자신의 이메일 주소를 입력
            String toMail = member.getUsername();
            String title = "비밀번호 찾기 이메일 입니다."; // 이메일 제목
            String content =
                    " <div" +
                            "	style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 400px; height: 600px; border-top: 4px solid #00CFFF; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">" +
                            "	<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">" +
                            "		<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">너나만나</span><br />" +
                            "		<span style=\"color: #00CFFF\">메일인증</span> 안내입니다." +
                            "	</h1>\n" +
                            "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">" +
                            "		안녕하세요.<br />" +
                            toMail +
                            "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">" +
                            "		님<br />" +
                            "		너나만나의 비밀번호 찾기입니다.<br />" +
                            "		<b style=\"color: #00CFFF\">'인증 번호'</b> 를 입력하여 비밀번호 찾기를 완료해 주세요.<br />" +
                            "		감사합니다." +
                            "	</p>" +
                            "          <div style=\"text-align: center;\"><h1><b style=\"color: #00CFFF\" >" + redisUtil.getData("passAuth" + username) + "<br /><h1></div>" +
                            "	<div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>" +
                            "<br>" +
                            "남은 인증 횟수 : " + restCnt +
                            " </div>";
            emailService.mailSend(setFrom, toMail, title, content);
            return "남은 횟수 :" + restCnt;
        }
        return "인증 횟수를 초과하였습니다. 1시간 뒤에 다시 시도해 주세요.";
    }

    // 인증 체크
    public String findCheck(String authNum, String username) {
        return String.valueOf(authNum.equals(redisUtil.getData("passAuth" + username)));
    }

    // 비밀번호 변경
    @Transactional
    public String passChange(UserDetailsImpl userDetails, PasswordChangeDto passwordChangeDto) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordCheck())) {
            throw new CustomException(ErrorCode.PASSWORD_PASSWORDCHECK);
        } else {
            member.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            memberRepository.save(member);
        }
        return "비밀번호 변경 완료";
    }

    // password 변경
    @Transactional
    public String changepass(UserDetailsImpl userDetails, PasswordChangeDto passwordChangeDto) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        if (!passwordEncoder.matches(passwordChangeDto.getPasswordCheck(), userDetails.getPassword()) && !passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordCheck())) {
            throw new CustomException(ErrorCode.PASSWORD_PASSWORDCHECK);
        } else {
            member.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            memberRepository.save(member);
        }
        return "비밀번호 변경 완료";
    }

    public void join(MemberRequestDto requestDto) {
        Member member = new Member(requestDto.getUsername(), passwordEncoder.encode(requestDto.getPassword()));
        memberRepository.save(member);
    }

    // 유저의 닉네임으로 유저 조회
    public Member getMember(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }

    @Transactional
    public void logout(UserDetailsImpl userDetails) {
        redisUtil.deleteData(userDetails.getUsername() + JwtProperties.HEADER_REFRESH);
    }
}
