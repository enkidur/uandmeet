package com.project.uandmeet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {


    COMPLETED_OK(HttpStatus.OK,"수행 완료."),
    USERNAME_LEGNTH(HttpStatus.CONFLICT, "ID를 4자이상으로 만들어주세요."),
    USERNAME_EMAIL(HttpStatus.CONFLICT, "ID를 이메일 형식으로 만들어주세요."),
    PASSWORD_CONTAINUSERNAME(HttpStatus.BAD_REQUEST, "패스워드에 아이디가 들어갈 수 없습니다."),
    PASSWORD_PASSWORDCHECK(HttpStatus.BAD_REQUEST, "패스워드와 패스워드 체크가 맞지 않습니다."),
    // 400 Bad Request
    MEMBER_HAS_FULL(HttpStatus.CONFLICT,"참여할 수 있는 최대 인원을 초과했습니다"),
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST,"필수입력값이 없습니다."),
    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "만료되었거나 유효하지 않은 토큰입니다."),
    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    INVALID_AUTHORITY(HttpStatus.FORBIDDEN,"권한이 없는 사용자 입니다"),
    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),

    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다"),
    AUTH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다"),
    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "중복된 사용자명이 존재합니다"),
    /*유저의 이름을 4자이상으로 만들어주세요 */
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임이 존재합니다"),
//회원가입시 에러코드
    EMPTY_USERNAME(HttpStatus.BAD_REQUEST,"이메일을 입력해주세요"),
    USERNAME_WRONG(HttpStatus.CONFLICT, "아이디는 이메일 형식으로 입력해주세요"),
    MEMBER_PASSWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 아이디,비밀번호를 다시 확인해주세요."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일이 존재합니다"),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호를 입력해주세요."),
    PASSWORD_LEGNTH(HttpStatus.CONFLICT, "비밀번호는 6자 이상 12자 이하여야 합니다"),
    PASSWORD_WRONG(HttpStatus.CONFLICT, "비밀번호는 영문, 숫자, 특수문자를 포함해야합니다"),
    PASSWORD_CHECK(HttpStatus.CONFLICT, "비밀번호가 일치하지 않습니다."),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST,"닉네임을 입력해주세요."),
    NICKNAME_LEGNTH(HttpStatus.CONFLICT, "닉네임은 최소 2자 이상 10자 이하여야 합니다"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),


    /*FRIEND 관련 에러코드*/
    /*유저 이름이 같을 때 보내는 코드*/
    FRIENDNAME_OVERLAP(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "이미 친구로 등록 하였습니다."),
    SELF_REGISTRATION(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "자기자신을 등록 할 수 없습니다."),

    // 채팅 방에 관한 로직
    CAN_NOT_CREATE_ROOM(HttpStatus.BAD_REQUEST, "채팅 방을 생성할 수 없습니다"),
    DUPLICATE_CHAT_ROOM(HttpStatus.BAD_REQUEST, "채팅 방이 이미 존재합니다"),

    // 서버 오류관련
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 요청사항을 수행할 수 없습니다."),

    //Entry 관련
    ACCEPTED_SEAM(HttpStatus.ACCEPTED, "중복입니다");
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

}
