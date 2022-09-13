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
    private final ReviewRepository reviewRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikedRepository likedRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Uploader s3Uploader;
    private final String POST_IMAGE_DIR = "static";
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

        if (passwordEncoder.matches(password,userDetails.getPassword())) {
            String username = userDetails.getUsername();

            List<Entry> entries = entryRepository.findByMember(userDetails.getMember());
            List<Liked> likeds = likedRepository.findByMember(userDetails.getMember());

            //매칭 참여 했던 것들 지우기.
            for (Entry entry : entries) {
                Board board = boardRepository.findById(entry.getBoard().getId())
                        .orElseGet(() -> null);

                if (board != null) {
                    entryRepository.delete(entry);
                    board.setCurrentEntry(board.getCurrentEntry() - 1);
                    boardRepository.save(board);
                }

            }

            //좋아요 참여 했던 것들 지우기.
            for (Liked liked : likeds) {
                Board board = boardRepository.findById(liked.getBoard().getId())
                        .orElseGet(() -> null);

                if (board != null) {
                    likedRepository.delete(liked);
                    board.setLikeCount(board.getLikeCount() - 1);
                    boardRepository.save(board);
                }
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

//    public void accessAndRefreshTokenProcess(String username) {
//        String refreshToken = jwtTokenProvider.createRefreshToken();
//        redisUtil.setValues(refreshToken, username);
//        redisUtil.setExpire(refreshToken, 7 * 24 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS);
//        jwtTokenProvider.createToken(username);
//    }

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
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
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

//        accessTokenResponseMap.put(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
//        Map<String, String> tokens = accessTokenResponseMap;
        response.setHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
//        if (tokens.get(JwtProperties.HEADER_REFRESH) != null) {
//            response.setHeader(JwtProperties.HEADER_REFRESH, tokens.get(JwtProperties.HEADER_REFRESH));
//        }
        return "재발급 성공";
    }

    public String findpassword(String username) {
        redisUtil.setDataExpire("passAuth" + username, makeRandomNumber(),60 * 3L);
        redisUtil.setDataExpire("passCnt" + username, String.valueOf(emailCnt),60 * 60L);
        if (Integer.parseInt(redisUtil.getData("Cnt" + username)) < 4) {
            redisUtil.setDataExpire("Cnt" + username, String.valueOf(emailCnt + 1),60 * 60L);
            int restCnt = 3 - Integer.parseInt(redisUtil.getData("Cnt" + username));

            Member member = memberRepository.findByUsername(username).orElseThrow(
                    () -> new IllegalArgumentException("해당 아이디가 없습니다.")
            );

            //인증메일 보내기
            String setFrom = "wjdgns5488@naver.com"; // email-config에 설정한 자신의 이메일 주소를 입력
            String toMail = username;
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

            return "인증 번호 :" + redisUtil.getData("passAuth" + username) + "남은 횟수 :" + restCnt;
        }
        return "인증 횟수를 초과하였습니다. 1시간 뒤에 다시 시도해 주세요.";
    }

    // 인증 체크
    public String findCheck(String authNum, String username) {
        return String.valueOf(authNum.equals(redisUtil.getData("passAuth" + username)));
    }

    // 비밀번호 변경
    public String passChange(UserDetailsImpl userDetails, PasswordChangeDto passwordChangeDto) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("해당 권한이 없습니다.")
        );
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordCheck())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        } else {
            member.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        }
        return "비밀번호 변경 완료";
    }

    // 활동 내역 조회
    public MypageDto action(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("볼수 없는 정보입니다")
        );
        List<Entry> entry = entryRepository.findByMember(member); // 참여한 매칭 리스트
        String nickname = member.getNickname();
        List<String> concern = member.getConcern();
        Long cnt = entryRepository.countByMember(member); // 참여한 매칭
        Map<String, Long> joinCnt = new HashMap<>();
        if (cnt == 0) {
            return new MypageDto(nickname, concern);
        } else {
            for (int i = 0; i < cnt; i++) {
                String category = entry.get(i).getCategory().getCategory();
                Long categoryCnt = entryRepository.countByMemberAndCategory(member, entry.get(i).getCategory());
                joinCnt.put(category, categoryCnt);
            }
            return new MypageDto(nickname, concern, joinCnt);
        }
    }


        // 활동내역 -> 관심사 수정
        public MypageDto concernedit (UserDetailsImpl userDetails, String[]concerns){
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("수정 권한이 없습니다.")
            );
            List<Entry> entry = entryRepository.findByMember(member); // 참여한 매칭 리스트
            Long cnt = entryRepository.countByMember(member); // 참여한 매칭 수
            String nickname = member.getNickname(); // 고민중
            List<String> concern = new ArrayList<>(); // 초기화
            int idx = 0;
            for (String e : concerns) {
                if (idx > 2) {
                    break;
                }
                concern.add(e);
                idx++;
            }
            member.setConcern(concern);
            Map<String, Long> joinCnt = new HashMap<>();
            if (cnt == 0) {
                System.out.println(concern);
                log.info(String.valueOf(concern));
                return new MypageDto(nickname, concern);
            }
            for (int i = 0; i < cnt; i++) {
                String category = entry.get(i).getCategory().getCategory();
                Long categoryCnt = entryRepository.countByMemberAndCategory(member, entry.get(i).getCategory());
                joinCnt.put(category, categoryCnt);
            }
            return new MypageDto(nickname, concern, joinCnt);
        }


        // 활동 페이지 -> 닉네임 수정
        public MypageDto nicknameedit (UserDetailsImpl userDetails, String nickname){
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("수정 권한이 없습니다.")
            );
            List<Entry> entry = entryRepository.findByMember(member); // 참여한 매칭 리스트
            Long cnt = entryRepository.countByMember(member); // 참여한 매칭
            List<String> concern = member.getConcern();
            Map<String, Long> joinCnt = new HashMap<>();
            for (int i = 0; i < cnt; i++) {
                if (entry.get(0).getBoard().getCategory().getCategory() == null) {
                    MypageDto mypageDto = new MypageDto(nickname, concern);
                    return mypageDto;
                } else {
                    String category = entry.get(i).getCategory().getCategory();
                    Long categoryCnt = entryRepository.countByMemberAndCategory(member, entry.get(i).getCategory());
                    joinCnt.put(category, categoryCnt);
                }
            }
            Member usingnickname = memberRepository.findByNickname(nickname).orElse(null);
            if (usingnickname == null) {
                member.setNickname(nickname);
            } else {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }
            MypageDto mypageDto = new MypageDto(nickname, concern, joinCnt);
            return mypageDto;
        }

        // memberInfo 조회
        public MyPageInfoDto myinfo (UserDetailsImpl userDetails){
            String username = userDetails.getUsername();
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("볼 수 없는 정보입니다")
            );
            String gender = member.getGender();
            Map<String, Long> birth = member.getBirth(); // year, month, day
            MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
            return myPageInfoDto;
        }

        // info -> gender 수정
        public MyPageInfoDto genderedit (UserDetailsImpl userDetails, InfogenderDto requestDto){
            String username = userDetails.getUsername();
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("볼 수 없는 정보입니다")
            );
            String gender = requestDto.getGender();
            member.setGender(gender);
            Map<String, Long> birth = member.getBirth();
            MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
            return myPageInfoDto;
        }

        // info -> birth 수정
        public MyPageInfoDto birthedit (UserDetailsImpl userDetails, InfoeditRequestDto requestDto){
            String username = userDetails.getUsername();
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("볼 수 없는 정보입니다")
            );
            String gender = member.getGender();
            Map<String, Long> birth = new HashMap<>();
            birth.put("birthYear", requestDto.getBirthYear());
            birth.put("birthMonth", requestDto.getBirthMonth());
            birth.put("birthDay", requestDto.getBirthDay());
            member.setBirth(birth);
            MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
            return myPageInfoDto;
        }

        // profile 조회
        public ProfileDto profile (UserDetailsImpl userDetails){
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("볼수 없는 정보입니다")
            );
            List<Review> review = reviewRepository.findByTo(userId);

            String nickname = member.getNickname();
            Double sum = 0D;
            Double star;
            for (Review value : review) {
                sum += value.getEvaluation_items();
            }
            if (sum == 0) {
                star = 0D;
            } else {
                star = sum / review.size(); // 평균 별점
            }
            String profile = member.getProfile();
            ProfileDto profileDto = new ProfileDto(nickname, star, profile);
            return profileDto;
        }

        // profile 수정
        public ProfileDto profileedit (UserDetailsImpl userDetails, ProfileEditRequestDto requestDto) throws IOException
        {
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
            );
            List<Review> review = reviewRepository.findByTo(userId);
            String nickname = member.getNickname();
            Double sum = 0D;
            Double star;
            for (Review value : review) {
                sum += value.getEvaluation_items();
            }
            if (sum == 0) {
                star = 0D;
            } else {
                star = sum / review.size(); // 평균 별점
            }
            if (requestDto.getData() != null) {
                ImageDto uploadImage = s3Uploader.upload(requestDto.getData(), POST_IMAGE_DIR);
                member.setProfile(uploadImage.getImageUrl());
                ProfileDto profileDto = new ProfileDto(nickname, star, uploadImage.getImageUrl());
                return profileDto;
            } else {
                ProfileDto profileDto = new ProfileDto(nickname, star);
                return profileDto;
            }
        }

        // password 변경
        public String changepass (UserDetailsImpl userDetails, PasswordChangeDto passwordChangeDto){
            Long userId = userDetails.getMember().getId();
            Member member = memberRepository.findById(userId).orElseThrow(
                    () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
            );
            if (!passwordEncoder.encode(passwordChangeDto.getPasswordCheck()).equals(member.getPassword()) && !passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordCheck())) {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            } else {
                member.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            }
            return "비밀번호 변경 완료";
        }

        public void join (MemberRequestDto requestDto){
            Member member = new Member(requestDto.getUsername(), passwordEncoder.encode(requestDto.getPassword()));
            memberRepository.save(member);
        }

        public SimpleReviewResponseDto simpleReview (Long memberId){
            Member member = memberRepository.findById(memberId).orElseThrow(
                    () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
            );
            Map<Integer, Long> reviews = new LinkedHashMap<>();
            Map<Integer, Long> sortedReview = new LinkedHashMap<>();
            Long reviewCnt = reviewRepository.countByTo(member.getId());
            System.out.println(reviewCnt);
            for (int i = 0; i < reviewCnt; i++) {
                Long numCnt = reviewRepository.countByToAndNum(member.getId(), i);
                reviews.put(i, numCnt);
            }
            List<Map.Entry<Integer, Long>> highs =
                    reviews.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
            if (reviewCnt < 6) {
                for (int i = Math.toIntExact(reviewCnt)-1 ; i >= 0; i--) {
                    Map.Entry<Integer, Long> high = highs.get(i);
                    sortedReview.put(high.getKey(), high.getValue());
                }
            } else {
                for (int i = Math.toIntExact(reviewCnt) - 1; i > reviewCnt - 6; i--) {
                    Map.Entry<Integer, Long> high = highs.get(i);
                    System.out.println("key" + high.getKey() + "value" + high.getValue());
                    sortedReview.put(high.getKey(), high.getValue());
                }
            }
            return new SimpleReviewResponseDto(sortedReview);
        }

        public List<Review> Review (Long memberId){
            return reviewRepository.findAllById(memberId);
        }

        public MyPostInfoResponseDto mypostinformation (UserDetailsImpl userDetails,int page, int amount){
            // page 함수
            Sort.Direction direction = Sort.Direction.ASC;
            String sortby = "createdAt";
            Sort sort = Sort.by(direction, sortby);
            Pageable pageable = PageRequest.of(page, amount, sort);

            Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                    () -> new RuntimeException("찾을 수 없는 사용자입니다.")
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
                        myListMemberResponseDto);
                boardInfo.add(responseDto);
            }
            Long informationCount = boardRepository.countByMemberAndAndBoardType(member, "information");
            return new MyPostInfoResponseDto(informationCount, boardInfo);
        }


        public MypostResponseDto mypostmatching (UserDetailsImpl userDetails,int page, int amount){
            // page 함수
            Sort.Direction direction = Sort.Direction.ASC;
            String sortby = "createdAt";
            Sort sort = Sort.by(direction, sortby);
            Pageable pageable = PageRequest.of(page, amount, sort);

            Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                    () -> new RuntimeException("찾을 수 없는 사용자입니다.")
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
                        myListMemberResponseDto);
                boardInfo.add(responseDto);
            }
            Long matchingCount = boardRepository.countByMemberAndAndBoardType(member, "matching");
            return new MypostResponseDto(matchingCount, boardInfo);
        }

        public MypostResponseDto myentry (UserDetailsImpl userDetails,int page, int amount){
            // page 함수
            Sort.Direction direction = Sort.Direction.ASC;
            String sortby = "createdAt";
            Sort sort = Sort.by(direction, sortby);
            Pageable pageable = PageRequest.of(page, amount, sort);

            Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                    () -> new RuntimeException("찾을 수 없는 사용자입니다.")
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
                        myListMemberResponseDto);
                boardInfo.add(responseDto);
            }
            Long totalCount = entryRepository.countByMember(member);
            return new MypostResponseDto(totalCount, boardInfo);
        }

        public MypostCommentResponseDto mycommentinformation (UserDetailsImpl userDetails,int page, int amount){
            // page 함수
            Sort.Direction direction = Sort.Direction.ASC;
            String sortby = "createdAt";
            Sort sort = Sort.by(direction, sortby);
            Pageable pageable = PageRequest.of(page, amount, sort);

            Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                    () -> new RuntimeException("찾을 수 없는 사용자입니다.")
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
            Sort.Direction direction = Sort.Direction.ASC;
            String sortby = "createdAt";
            Sort sort = Sort.by(direction, sortby);
            Pageable pageable = PageRequest.of(page, amount, sort);

            Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                    () -> new RuntimeException("찾을 수 없는 사용자입니다.")
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
                        comment.getComment(),
                        comment.getBoardType(),
                        myListMemberResponseDto);
                commentList.add(responseDto);
            }
            Long matchingCount = commentRepository.countByMemberAndBoardType(member, "matching");
            return new MypostCommentResponseDto(matchingCount, commentList);
        }


        // 유저의 닉네임으로 유저 조회
        public Member getMember (String nickname){
            return memberRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
        }

        public void logout (UserDetailsImpl userDetails){
            redisUtil.deleteData(userDetails.getUsername() + JwtProperties.HEADER_REFRESH);
        }
    }
