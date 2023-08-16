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
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");


    private final HttpStatus status;
    private final String message;
}