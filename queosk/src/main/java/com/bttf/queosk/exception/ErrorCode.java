package com.bttf.queosk.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // ex) NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원 정보를 찾을 수 없습니다.")

    //UserService 관련 Exception
    INVALID_USER_ID(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."),
    EXISTING_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
    NICKNAME_NOT_MATCH(HttpStatus.BAD_REQUEST,"닉네임 정보가 일치가하지 않습니다." ),

    // Table 관련 Exception
    INVALID_TABLE(HttpStatus.NOT_FOUND, "존재하지 않는 테이블입니다."),
    // Restaurant 관련 Exception
    INVALID_RESTAURANT(HttpStatus.NOT_FOUND, "존재하지 않는 상점입니다."),
    WITHDRAWN_USER(HttpStatus.BAD_REQUEST,"이미 탈퇴한 회원입니다." ),
    NOT_VERIFIED_USER(HttpStatus.BAD_REQUEST,"이메일 검증 진행 후 로그인이 가능합니다." ),
    INVALID_REFRESH_TOKEN(HttpStatus.NOT_FOUND,"존재하지 않는 리프레시 토큰입니다." ),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"토큰이 존재하지 않습니다." );


    private final HttpStatus status;
    private final String message;
}