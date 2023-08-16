package com.bttf.queosk.service.userService;

import com.bttf.queosk.dto.userDto.UserSignInDto;
import com.bttf.queosk.dto.userDto.UserSignInForm;
import com.bttf.queosk.dto.userDto.UserSignUpForm;

public interface UserService {
    void createUser(UserSignUpForm userSignUpForm);

    UserSignInDto signInUser(UserSignInForm userSignInForm);

    boolean checkDuplication(String email);
}
