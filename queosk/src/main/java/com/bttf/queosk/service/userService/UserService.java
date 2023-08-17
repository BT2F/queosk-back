package com.bttf.queosk.service.userService;

import com.bttf.queosk.dto.userDto.UserDto;
import com.bttf.queosk.dto.userDto.UserSignInDto;
import com.bttf.queosk.dto.userDto.UserSignInForm;
import com.bttf.queosk.dto.userDto.UserSignUpForm;

public interface UserService {

    //사용자 회원가입
    void createUser(UserSignUpForm userSignUpForm);

    //사용자 로그인
    UserSignInDto signInUser(UserSignInForm userSignInForm);

    //이메일 중복 검증
    boolean checkDuplication(String email);

    //토큰으로 사용자 정보조회
    UserDto getUserFromToken(String token);

    //아이디로 사용자 정보조회
    UserDto getUserFromId(Long id);

}
