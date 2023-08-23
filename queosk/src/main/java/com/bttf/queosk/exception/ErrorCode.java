package com.bttf.queosk.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // ex) NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원 정보를 찾을 수 없습니다.")
    // 공통 Exception
    NOT_PERMITTED(HttpStatus.NOT_ACCEPTABLE, "접근 권한이 없습니다."),

    // UserService 관련 Exception
    INVALID_USER_ID(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."),
    EXISTING_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
    NICKNAME_NOT_MATCH(HttpStatus.BAD_REQUEST,"닉네임 정보가 일치가하지 않습니다." ),
    WITHDRAWN_USER(HttpStatus.BAD_REQUEST,"이미 탈퇴한 회원입니다." ),
    NOT_VERIFIED_USER(HttpStatus.BAD_REQUEST,"이메일 검증 진행 후 로그인이 가능합니다." ),
    KAKAO_LOGIN_FAILED(HttpStatus.BAD_REQUEST,"카카오 소셜 로그인 도중 예외가 발생했습니다."),
    KAKAO_USER_UNSUPPORTED_SERVICE(HttpStatus.BAD_REQUEST,"카카오 소셜 로그인 회원이게는 지원되지 않는 기능입니다." ),

    // Token 관련 Exception
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"만료되거나 유효하지 않은 토큰입니다." ),

    // Table 관련 Exception
    INVALID_TABLE(HttpStatus.NOT_FOUND, "존재하지 않는 테이블입니다."),

    // Wating 관련 Exception
    INVALID_WATING(HttpStatus.NOT_FOUND, "존재하지 않는 웨이팅입니다."),

    // Restaurant 관련 Exception
    INVALID_RESTAURANT(HttpStatus.NOT_FOUND, "존재하지 않는 상점입니다."),
    OWNER_NAME_NOT_MATCH(HttpStatus.BAD_REQUEST, "매장 계정 사용자 이름이 일치 하지 않습니다."),

    // Cart 관련 Exception
    CART_IS_EMPTY(HttpStatus.NOT_FOUND, "장바구니가 비어있습니다."),
    DIFFERENT_RESTAURANT(HttpStatus.BAD_REQUEST, "같은 식당의 메뉴만 장바구니에 담을 수 있습니다. " +
            "장바구니를 초기화하고 선택하신 식당의 메뉴를 담으시겠습니까?"),
    CART_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 기존 장바구니가 존재합니다."),

    // Menu 관련 Exception
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND,"아직 해당 식당에 대한 메뉴가 등록되지 않습니다."),
    UNAUTHORIZED_SERVICE(HttpStatus.BAD_REQUEST, "본인매장의 메뉴만 수정할 수 있습니다.");


    private final HttpStatus status;
    private final String message;
}