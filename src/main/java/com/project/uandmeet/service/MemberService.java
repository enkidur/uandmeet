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
    private int emailCnt = 0; // email ?????? ??????

    // ?????? ??????
    public String makeRandomNumber() {
        String checkNum = UUID.randomUUID().toString().substring(0, 6);
        System.out.println("?????? ???????????? : " + checkNum);
        return checkNum;
    }

    // ?????? ?????? 1. emali check
    public String checkemail(String username) {
        checkEmail(username);

        // email ?????? ??????
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
        // email ??????
        // ID ?????? ????????????, ??????, _!#$%&'\*+/=?{|}~^.- ????????????
        // Host ????????? @, ?????? ????????????, ??????, .-????????????

        // ???????????? username ??????
        if (username.length() < 10) {
            throw new IllegalArgumentException("???????????? 10??? ?????? ???????????????");
        } else if (!Pattern.matches(idpattern, id)) {
            throw new IllegalArgumentException("id??? ????????? ??????????????? ??????, ????????????( _!#$%&'\\*+/=?{|}~^.-)?????? ???????????????");
        } else if (!Pattern.matches(hostpattern, host)) {
            throw new IllegalArgumentException("host??? ????????? ??????????????? ??????, ????????????(.-)?????? ???????????????");
        } else if (!Pattern.matches(pattern, username)) {
            throw new IllegalArgumentException("????????? ????????? ?????? ???????????????");
        } else if (username.contains("script")) {
            throw new IllegalArgumentException("xss?????? ???????????????.");
        }
    }


    // ????????????, ???????????? ????????? ??????
    public String checkPassword(String password, String passwordCheck) {
        if (password.length() < 3) {
            throw new IllegalArgumentException("??????????????? 3??? ?????? ???????????????");
        } else if (password.length() > 21) {
            throw new IllegalArgumentException("??????????????? 20??? ????????? ???????????????");
        } else if (password.contains("script")) {
            throw new IllegalArgumentException("xss?????? ???????????????.");
        } else if (passwordCheck.contains("script")) {
            throw new IllegalArgumentException("xss?????? ???????????????.");
        }
        if (!(passwordCheck.equals(password))) {
            throw new IllegalArgumentException("??????????????? ???????????? ????????????.");
        }
        return "password check ??????";
    }

    @Transactional
    public String signup(MemberRequestDto requestDto) {
        String username = requestDto.getUsername();
        String[] emailadress = username.split("@");
        String id = emailadress[0];
        String uuid = UUID.randomUUID().toString().substring(0, 5);
        String uniqueId = id + uuid;
        // ????????? ?????? ??????
        checkEmail(username);
        // ????????? ?????? ??????
        checkDuplicateEmail(username);
        checkPassword(requestDto.getPassword(), requestDto.getPasswordCheck());
        Member member = requestDto.register();
        member.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        // login ??????
        member.setLoginto("normal");
        // ?????? ?????????
        member.setNickname(uniqueId);

        // ????????? ????????? ??????
//        if (requestDto.getUserProfileImage() != null) {
//            String profileUrl = s3Uploader.upload(requestDto.getUserProfileImage(), "profile");
//            users.setUserProfileImage(profileUrl);
//        }

        memberRepository.save(member);
        return "???????????? ??????";
    }


    public void checkDuplicateEmail(String username) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }


    // ?????? ??????
    @Transactional
    public ResponseEntity<String> withdraw(UserDetailsImpl userDetails, String password) {
        ResponseEntity<String> responseEntity = null;

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            String username = userDetails.getUsername();

            List<Entry> entries = entryRepository.findByMember(userDetails.getMember());
            List<Liked> likeds = likedRepository.findByMember(userDetails.getMember());
            List<Comment> commentList = commentRepository.findByMember(userDetails.getMember());

            //?????? ?????? ?????? ?????? ?????????.
            for (Entry entry : entries) {
                Board board = boardRepository.findById(entry.getBoard().getId())
                        .orElseGet(() -> null);

                if (board != null) {
                    board.setCurrentEntry(board.getCurrentEntry() - 1);
                    boardRepository.save(board);
                }
                entryRepository.delete(entry);
            }

            //????????? ?????? ?????? ?????? ?????????.
            for (Liked liked : likeds) {
                Board board = boardRepository.findById(liked.getBoard().getId())
                        .orElseGet(() -> null);

                if (board != null) {
                    board.setLikeCount(board.getLikeCount() - 1);
                    boardRepository.save(board);
                }

                likedRepository.delete(liked);
            }

            //?????? ?????????
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
                responseEntity = ResponseEntity.ok("?????? ?????? ??????.");
            }catch (Exception e)
            {
                System.out.println(e);
                responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.valueOf(e));
            }
        }
        else
            responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("????????? ?????????????????????.");

        return responseEntity;
    }


    @Transactional
    public String refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //AccessToken
        String expiredAccessTokenHeader = request.getHeader(JwtProperties.HEADER_ACCESS);
//        String expiredAccessToken = jwtTokenProvider.setTokenName(expiredAccessTokenHeader); // barrer ??????
        if (expiredAccessTokenHeader == null || !expiredAccessTokenHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new CustomException(ErrorCode.EMPTY_CONTENT);
        }
        String expiredAccessTokenName = jwtTokenProvider.getExpiredAccessTokenPk(expiredAccessTokenHeader);
        // refreshToken
        String authorizationHeader = redisUtil.getData(expiredAccessTokenName + JwtProperties.HEADER_REFRESH);

        // ?????? ???????????? ??????
        if (!redisUtil.getData(expiredAccessTokenName + JwtProperties.HEADER_ACCESS).equals(expiredAccessTokenHeader)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Refresh Token ????????? ??????
        jwtTokenProvider.validateToken(authorizationHeader);
        String username = jwtTokenProvider.getUserPk(authorizationHeader);
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        Long userId = member.getId();
        // Access Token ?????????
        String accessToken = jwtTokenProvider.createToken(username, userId);

//        Map<String, String> accessTokenResponseMap = new HashMap<>();

        // ??????????????? Refresh Token ??????????????? ?????? ?????? ???????????? ?????? (???????????? ??? ???????????? ??????????????? ??????)
        // Refresh Token ???????????? ????????? ?????? ?????? ????????? ??? refresh token ??? ?????????
        Date now = new Date();
        Date refreshExpireTime = jwtTokenProvider.ExpireTime(authorizationHeader);
        if (refreshExpireTime.before(new Date(now.getTime() + 1000 * 60 * 60 * 24L))) { // refresh token ??????????????? ?????????????????? ????????? ?????????
            String newRefreshToken = jwtTokenProvider.createRefreshToken(username);
//            accessTokenResponseMap.put(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + newRefreshToken);
            redisUtil.setDataExpire(jwtTokenProvider.getUserPk(accessToken) + JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken, JwtProperties.ACCESS_EXPIRATION_TIME);
            redisUtil.setDataExpire(jwtTokenProvider.getUserPk(accessToken) + JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + newRefreshToken, JwtProperties.REFRESH_EXPIRATION_TIME);
        }

        response.setHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        return "????????? ??????";
    }

    @Transactional
    public String findpassword(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        redisUtil.setDataExpire("passAuth" + username, makeRandomNumber(), 60 * 3L);
        redisUtil.setDataExpire("passCnt" + username, String.valueOf(emailCnt), 60 * 60L);
        if (Integer.parseInt(redisUtil.getData("passCnt" + username)) < 4) {
            redisUtil.setDataExpire("passCnt" + username, String.valueOf(emailCnt + 1), 60 * 60L);
            int restCnt = 3 - Integer.parseInt(redisUtil.getData("passCnt" + username));


            //???????????? ?????????
            String setFrom = "wkraudcka@naver.com"; // email-config??? ????????? ????????? ????????? ????????? ??????
            String toMail = member.getUsername();
            String title = "???????????? ?????? ????????? ?????????."; // ????????? ??????
            String content =
                    " <div" +
                            "	style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 400px; height: 600px; border-top: 4px solid #00CFFF; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">" +
                            "	<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">" +
                            "		<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">????????????</span><br />" +
                            "		<span style=\"color: #00CFFF\">????????????</span> ???????????????." +
                            "	</h1>\n" +
                            "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">" +
                            "		???????????????.<br />" +
                            toMail +
                            "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">" +
                            "		???<br />" +
                            "		??????????????? ???????????? ???????????????.<br />" +
                            "		<b style=\"color: #00CFFF\">'?????? ??????'</b> ??? ???????????? ???????????? ????????? ????????? ?????????.<br />" +
                            "		???????????????." +
                            "	</p>" +
                            "          <div style=\"text-align: center;\"><h1><b style=\"color: #00CFFF\" >" + redisUtil.getData("passAuth" + username) + "<br /><h1></div>" +
                            "	<div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>" +
                            "<br>" +
                            "?????? ?????? ?????? : " + restCnt +
                            " </div>";
            emailService.mailSend(setFrom, toMail, title, content);
            return "?????? ?????? :" + restCnt;
        }
        return "?????? ????????? ?????????????????????. 1?????? ?????? ?????? ????????? ?????????.";
    }

    // ?????? ??????
    public String findCheck(String authNum, String username) {
        return String.valueOf(authNum.equals(redisUtil.getData("passAuth" + username)));
    }

    // ???????????? ??????
    @Transactional
    public String passChange(PasswordChangeDto passwordChangeDto) {
        Member member = memberRepository.findByUsername(passwordChangeDto.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordCheck())) {
            throw new CustomException(ErrorCode.PASSWORD_PASSWORDCHECK);
        } else {
            member.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            memberRepository.save(member);
        }
        return "???????????? ?????? ??????";
    }

    // password ??????
    @Transactional
    public String changepass(UserDetailsImpl userDetails, PasswordChangeDto passwordChangeDto) {
        Member member = memberRepository.findByUsername(userDetails.getMember().getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        if (!passwordEncoder.matches(passwordChangeDto.getPasswordCheck(), userDetails.getPassword()) && !passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordCheck())) {
            throw new CustomException(ErrorCode.PASSWORD_PASSWORDCHECK);
        } else {
            member.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            memberRepository.save(member);
        }
        return "???????????? ?????? ??????";
    }

    public void join(MemberRequestDto requestDto) {
        Member member = new Member(requestDto.getUsername(), passwordEncoder.encode(requestDto.getPassword()));
        memberRepository.save(member);
    }

    // ????????? ??????????????? ?????? ??????
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
