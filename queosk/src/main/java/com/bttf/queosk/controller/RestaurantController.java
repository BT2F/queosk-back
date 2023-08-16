package com.bttf.queosk.controller;

import com.bttf.queosk.dto.restaurantDto.RestaurantSignInForm;
import com.bttf.queosk.service.RestaurantService.RestaurantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "Restaurant API", description = "매장 API")
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    private static ResponseEntity<?> getErrorResponse(Errors errors) {
        List<ObjectError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().forEach(e -> {
                responseErrorList.add(e);
            });
            return new ResponseEntity<>(responseErrorList,
                    HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @PostMapping("/signup")
    @ApiOperation(value = "사업자 회원가입")
    public ResponseEntity signIn(@Valid @RequestBody RestaurantSignInForm restaurantSignInForm, Errors errors) throws Exception {
        ResponseEntity<?> responseErrorList = getErrorResponse(errors);
        if (responseErrorList != null)
            throw new RuntimeException(); // TODO : 익셉션 제작
        restaurantService.signIn(restaurantSignInForm);
        return ResponseEntity.status(201).build();
    }
}
