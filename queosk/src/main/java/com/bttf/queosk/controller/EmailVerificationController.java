package com.bttf.queosk.controller;

import com.bttf.queosk.service.userservice.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Api(tags = "이메일 인증 API", description = "이메일 인증과 관련된 API")
@RequestMapping("/api/users")
@Controller
public class EmailVerificationController {
    private final UserInfoService userInfoService;
    @GetMapping("/{id}/verification")
    @ApiOperation(value = "사용자 회원인증", notes = "사용자 상태를 '인증' 상태로 변경합니다.")
    public String verifyUser(@PathVariable Long id, Model model) {

        String message = userInfoService.verifyUser(id);

        model.addAttribute("message", message);

        return "verification-result";
    }
}
