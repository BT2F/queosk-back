package com.bttf.queosk.controller;

import com.bttf.queosk.dto.AutoCompleteDto;
import com.bttf.queosk.dto.AutoCompleteResponse;
import com.bttf.queosk.service.AutoCompleteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RequestMapping("/api/autocomplete")
@Api(tags = "Auto-Complete API", description = "검색어 자동완성 리스트 조회 API")
@RestController
public class AutoCompleteController {

    private final AutoCompleteService autoCompleteService;

    @GetMapping
    @ApiOperation(value = "검색어 자동완성", notes = "주어진 prefix 로 식당이름을 자동완성합니다.")
    public ResponseEntity<AutoCompleteResponse> autoComplete(
            @RequestParam String prefix) {
        AutoCompleteDto autoCompleteDto = autoCompleteService.autoComplete(prefix);
        return ResponseEntity.status(OK).body(AutoCompleteResponse.of(autoCompleteDto));
    }
}


