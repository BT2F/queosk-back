package com.bttf.queosk.enumerate;

public class EmailComponents {
    public final static String VERIFICATION_SUBJECT = "Queosk 이메일 인증";
    public final static String VERIFICATION_TEXT =
            "Queosk에 가입해 주셔서 감사합니다.\n" +
                    "아래 링크를 클릭 하시어 이메일 인증을 완료해주세요.\n" +
                    "https://queosk.kr/api/users/%d/verification";

    public final static String PASSWORD_RESET_SUBJECT = "Queosk 비밀번호 재설정";
    public final static String PASSWORD_RESET_TEXT =
            "새로운 비밀번호 : %s를 입력해 로그인 해주세요.";
}