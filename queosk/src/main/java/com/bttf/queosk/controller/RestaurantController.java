package com.bttf.queosk.controller;

import com.bttf.queosk.domain.RestaurantSignInDto;
import com.bttf.queosk.service.RestaurantService;
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

    @PostMapping("/sign-in")
    @ApiOperation(value = "사업자 회원가입")
    public ResponseEntity<?> signIn(@Valid @RequestBody RestaurantSignInDto restaurantSignInDto, Errors errors) {
        ResponseEntity<?> responseErrorList = getErrorResponse(errors);
        if (responseErrorList != null) return responseErrorList;
        return ResponseEntity.status(201).body(restaurantService.signIn(restaurantSignInDto));
    }
}
