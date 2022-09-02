package com.project.uandmeet.service;

import com.project.uandmeet.dto.*;
import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.*;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.EntryRepository;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.security.jwt.JwtProperties;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import com.project.uandmeet.service.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final EntryRepository entryRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Uploader s3Uploader;
    private final String POST_IMAGE_DIR = "static";
    private String authNumber; // 난수 번호
    private int emailCnt; // email 인증 횟수

    // 난수 생성
    public void makeRandomNumber() {
        String checkNum = UUID.randomUUID().toString().substring(0, 6);
        System.out.println("임시 비밀번호 : " + checkNum);
        authNumber = checkNum;
    }

    // 회원 가입 1. emali check
    public String checkemail(String username) throws IOException {
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

    public String signup(MemberRequestDto requestDto) throws IOException {
        String username = requestDto.getUsername();
        String[] emailadress = username.split("@");
        String id = emailadress[0];
        String uuid = UUID.randomUUID().toString().substring(0, 3);
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
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        }
    }


    // 회원 탈퇴
    public String withdraw(UserDetailsImpl userDetails, String password) {
        if (userDetails.getPassword().equals(passwordEncoder.encode(password))) {
            String username = userDetails.getUsername();
            memberRepository.deleteByUsername(username);
        }
        return "회원탈퇴 완료";
    }

//    public void accessAndRefreshTokenProcess(String username) {
//        String refreshToken = jwtTokenProvider.createRefreshToken();
//        redisUtil.setValues(refreshToken, username);
//        redisUtil.setExpire(refreshToken, 7 * 24 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS);
//        jwtTokenProvider.createToken(username);
//    }

    public Map<String, String> refresh(HttpServletRequest request, HttpServletResponse response) {

        // refreshToken
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new RuntimeException("JWT Token이 존재하지 않습니다.");
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

        Map<String, String> accessTokenResponseMap = new HashMap<>();

        // 현재시간과 Refresh Token 만료날짜를 통해 남은 만료기간 계산 (만료기간 전 재발급이 필요없다면 삭제)
        // Refresh Token 만료시간 계산해 특정 시간 미만일 시 refresh token 도 재발급
        Date now = new Date();
        Date refreshExpireTime = jwtTokenProvider.ExpireTime(authorizationHeader);
        if (refreshExpireTime.before(new Date(now.getTime() + 1000 * 60 * 60 * 24L))) { // refresh token 만료시간이 특정시간보다 작으면 재발급
            String newRefreshToken = jwtTokenProvider.createRefreshToken();
            accessTokenResponseMap.put(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + newRefreshToken);
            redisUtil.setDataExpire(jwtTokenProvider.getUserPk(accessToken), newRefreshToken, JwtProperties.REFRESH_EXPIRATION_TIME);
        }

        accessTokenResponseMap.put(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        Map<String, String> tokens = accessTokenResponseMap;
        response.setHeader(JwtProperties.HEADER_ACCESS, tokens.get(JwtProperties.HEADER_ACCESS));
        if (tokens.get(JwtProperties.HEADER_REFRESH) != null) {
            response.setHeader(JwtProperties.HEADER_REFRESH, tokens.get(JwtProperties.HEADER_REFRESH));
        }
        return tokens;
    }

    public String findpassword(String username) {
        if (emailCnt < 4) {
            // 비밀번호 난수 생성
            makeRandomNumber();

            Member member = memberRepository.findByUsername(username).orElseThrow(
                    () -> new IllegalArgumentException("해당 아이디가 없습니다.")
            );
            member.setPassword(passwordEncoder.encode(authNumber));

            //인증메일 보내기
            String setFrom = "wjdgns5488@naver.com"; // email-config에 설정한 자신의 이메일 주소를 입력
            String toMail = username;
            String title = "비밀번호 찾기 이메일 입니다."; // 이메일 제목
            String content =
                    "홈페이지를 방문해주셔서 감사합니다." +    //html 형식으로 작성 !
                            "<br><br>" +
                            "비밀번호 찾기 인증번호는 " + authNumber + "입니다." +
                            "<br>" +
                            "인증 후 비밀번호를 변경해 주세요"; //이메일 내용 삽입
            emailService.mailSend(setFrom, toMail, title, content);
            emailCnt += 1;
            int restCnt = 3 - emailCnt;
            // 유효 시간(3분)동안 {fromEmail, authKey} 저장
            redisUtil.setDataExpire(authNumber, setFrom, 60 * 3L);
            // 횟수
            redisUtil.setDataExpire("Cnt" + authNumber, String.valueOf(emailCnt), 60 * 60L);
            // 유효 시간(1시간)동안 {toEmail, emailCnt} 저장
            redisUtil.setDataExpire(toMail, String.valueOf(emailCnt), 60 * 60L);
            return "인증 번호 :" + authNumber + "남은 횟수 :" + restCnt;
        }
        return "인증 횟수를 초과하였습니다. 1시간 뒤에 다시 시도해 주세요.";
    }

    // 인증 체크
    public String findCheck(String authNum) {
        return String.valueOf(authNum.equals(authNumber));
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
//        List<Entry> entry = entryRepository.findByMember(member); // 참여한 매칭 리스트
//        System.out.println("엔트리 :"+entry.get(0));
//        Long cnt = entryRepository.countByMember(member); // 참여한 매칭
//        System.out.println("참여 매칭 :"+cnt);
        String nickname = member.getNickname();
        List<String> concern = member.getConcern();
        Map<Category, Long> joinCnt = new HashMap<>();
//        for (int i = 0; i < cnt; i++) {
//            String category = String.valueOf(entry.get(i).getBoard().getCategory());
//            Long cCnt = entryRepository.countByBoard(entry.get(i).getBoard().getCategory());
//            System.out.println("카테고리 :"+category);
//            System.out.println(cCnt);
//            joinCnt.put(entry.get(i).getBoard().getCategory(), entryRepository.countByBoard(entry.get(i).getBoard().getCategory()));
//            System.out.println("조인 카운터 :"+joinCnt);
//        }
        MypageDto mypageDto = new MypageDto(nickname, concern, joinCnt);
        return mypageDto;
    }

    // 활동내역 -> 관심사 수정
    public MypageDto concernedit(UserDetailsImpl userDetails, String concern1, String concern2, String concern3) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("수정 권한이 없습니다.")
        );
        List<Entry> entry = entryRepository.findByMember(member); // 참여한 매칭 리스트
        Long cnt = entryRepository.countByMember(member); // 참여한 매칭 수
        String nickname = member.getNickname(); // 고민중
        List<String> concerns = new ArrayList<>(); // 초기화
//        for (int i = 0; i < concerns.size(); i++) {
//            Concern concern1 = concerns.get(i);
        concerns.add(concern1);
        concerns.add(concern2);
        concerns.add(concern3);

        member.setConcern(concerns);
        Map<Category, Long> joinCnt = new HashMap<>();
//        for (int i = 0; i < cnt; i++) {
//            joinCnt.put(entry.get(i).getBoard().getCategory(), entryRepository.countByBoard(entry.get(i).getBoard().getId()));
//        }
        MypageDto mypageDto = new MypageDto(nickname, concerns, joinCnt);
        return mypageDto;
    }

    // 활동 페이지 -> 닉네임 수정
    public MypageDto nicknameedit(UserDetailsImpl userDetails, String nickname) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("수정 권한이 없습니다.")
        );
        List<Entry> entry = entryRepository.findByMember(member); // 참여한 매칭 리스트
        Long cnt = entryRepository.countByMember(member); // 참여한 매칭
        List<String> concern = member.getConcern();
        Map<Category, Long> joinCnt = new HashMap<>();
        for (int i = 0; i < cnt; i++) {
            joinCnt.put(entry.get(i).getBoard().getCategory(), entryRepository.countByBoard(entry.get(i).getBoard().getId()));
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
    public MyPageInfoDto myinfo(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("볼 수 없는 정보입니다")
        );
        String gender = member.getGender();
        String birth = member.getBirth(); // year, month, day
        MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
        return myPageInfoDto;
    }

    // info -> gender 수정
    public MyPageInfoDto genderedit(UserDetailsImpl userDetails, InfoeditRequestDto requestDto) {
        String username = userDetails.getUsername();
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("볼 수 없는 정보입니다")
        );
        String gender = requestDto.getGender();
        String birth = member.getBirth();
        member.setGender(gender);
        MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
        return myPageInfoDto;
    }

    // info -> birth 수정
    public MyPageInfoDto birthedit(UserDetailsImpl userDetails, InfoeditRequestDto requestDto) {
        String username = userDetails.getUsername();
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("볼 수 없는 정보입니다")
        );
        String gender = member.getGender();
        String birth = requestDto.getBirth();
        member.setBirth(birth);
        MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
        return myPageInfoDto;
    }

    // profile 조회
    public ProfileDto profile(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("볼수 없는 정보입니다")
        );
        String nickname = member.getNickname();
        List<Star> star = member.getStar();
        String profile = member.getProfile();
        ProfileDto profileDto = new ProfileDto(nickname, star, profile);
        return profileDto;
    }

    // profile 수정
    public ProfileDto profileedit(UserDetailsImpl userDetails, ProfileEditRequestDto requestDto) throws IOException {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("볼수 없는 정보입니다")
        );
        String nickname = member.getNickname();
        List<Star> star = member.getStar();
        String uploadImage = String.valueOf(s3Uploader.upload(requestDto.getData(), POST_IMAGE_DIR));
        member.setProfile(uploadImage);
        ProfileDto profileDto = new ProfileDto(nickname, star, uploadImage);
        return profileDto;
    }

    // password 변경
    public String changepass(UserDetailsImpl userDetails, PasswordChangeDto passwordChangeDto) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("해당 권한이 없습니다.")
        );
        if (!passwordEncoder.encode(passwordChangeDto.getPasswordCheck()).equals(member.getPassword()) && !passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordCheck())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        } else {
            member.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        }
        return "비밀번호 변경 완료";
    }

    public void join(MemberRequestDto requestDto) {
        Member member = new Member(requestDto.getUsername(), passwordEncoder.encode(requestDto.getPassword()));
        memberRepository.save(member);
    }

    //채팅회원관련
    public Member findByNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        return member;
    }
}

