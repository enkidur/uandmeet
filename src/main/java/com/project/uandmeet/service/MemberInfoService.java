package com.project.uandmeet.service;

import com.project.uandmeet.dto.*;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Entry;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.Review;
import com.project.uandmeet.repository.EntryRepository;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.repository.ReviewRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberInfoService {
    private final MemberRepository memberRepository;
    private final EntryRepository entryRepository;
    private final ReviewRepository reviewRepository;
    private final S3Uploader s3Uploader;
    private final String POST_IMAGE_DIR = "static";
    // 활동 내역 조회
    public MypageDto action(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
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
    @Transactional
    public MypageDto concernedit (UserDetailsImpl userDetails, String[]concerns){
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        List<Entry> entry = entryRepository.findByMember(member); // 참여한 매칭 리스트
        Long cnt = entryRepository.countByMember(member); // 참여한 매칭 수
        String nickname = member.getNickname();
        List<String> concern = new ArrayList<>();
        int idx = 0;
        for (String e : concerns) {
            if (idx > 2) {
                break;
            }
            concern.add(e);
            idx++;
        }
        member.setConcern(concern);
        memberRepository.save(member);
        Map<String, Long> joinCnt = new HashMap<>();
        if (cnt == 0) {
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
    @Transactional
    public MypageDto nicknameedit (UserDetailsImpl userDetails, String nickname){
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
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
            memberRepository.save(member);
        } else {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        MypageDto mypageDto = new MypageDto(nickname, concern, joinCnt);
        return mypageDto;
    }

    // memberInfo 조회
    public MyPageInfoDto myinfo (UserDetailsImpl userDetails){
        String username = userDetails.getUsername();
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        String gender = member.getGender();
        Map<String, Long> birth = member.getBirth(); // year, month, day
        MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
        return myPageInfoDto;
    }

    // info -> gender 수정
    @Transactional
    public MyPageInfoDto genderedit (UserDetailsImpl userDetails, InfogenderDto requestDto){
        String username = userDetails.getUsername();
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        String gender = requestDto.getGender();
        member.setGender(gender);
        Map<String, Long> birth = member.getBirth();
        MyPageInfoDto myPageInfoDto = new MyPageInfoDto(username, gender, birth);
        return myPageInfoDto;
    }

    // info -> birth 수정
    @Transactional
    public MyPageInfoDto birthedit (UserDetailsImpl userDetails, InfoeditRequestDto requestDto){
        String username = userDetails.getUsername();
        Long userId = userDetails.getMember().getId();
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
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
        String profile = member.getProfile();
        ProfileDto profileDto = new ProfileDto(nickname, star, profile);
        return profileDto;
    }

    // profile 수정
    @Transactional
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
    public SimpleReviewResponseDto simpleReview (Long memberId){
        // 해당 유저가 없을 시 에러코드르 띄우기 위해 사용
        Member member = memberRepository.findById(memberId).orElseThrow(
                ()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        Long reviewCnt = reviewRepository.countByTo(member.getId());
        List<Review> review = reviewRepository.findByTo(member.getId());
        List<Integer> reviewNum = new ArrayList<>();
        Map<Integer, Integer> reviews = new LinkedHashMap<>();
        Map<Integer, Integer> sortedReview = new LinkedHashMap<>();


        for (int i = 0; i < reviewCnt; i++) {
            List<Integer> nums = review.get(i).getNum();
            reviewNum.addAll(nums);
        }

        for (int i = 0; i < 16; i++) {
            int numCnt = Collections.frequency(reviewNum, i);
            reviews.put(i, numCnt);
        }

        List<Map.Entry<Integer, Integer>> highs =
                reviews.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(Collectors.toList());

        for (int i = 0; i < 6; i++) {
            Map.Entry<Integer, Integer> high = highs.get(i);
            sortedReview.put(high.getKey(), high.getValue());
        }

        return new SimpleReviewResponseDto(sortedReview);
    }

    public List<Review> Review (Long memberId){
        return reviewRepository.findAllById(memberId);
    }


}
