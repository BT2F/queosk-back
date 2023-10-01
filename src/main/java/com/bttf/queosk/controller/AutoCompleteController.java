package com.bttf.queosk.controller;

import com.bttf.queosk.dto.AutoCompleteDto;
import com.bttf.queosk.dto.AutoCompleteResponseForm;
import com.bttf.queosk.service.AutoCompleteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RequestMapping("/api/autocomplete")
@Api(tags = "Auto-Complete API", description = "검색어 자동완성 리스트 조회 API")
@RestController
public class AutoCompleteController {

    private final AutoCompleteService autoCompleteService;

    @GetMapping
    @ApiOperation(value = "검색어 자동완성", notes = "주어진 검색어의 일부로 식당이름을 자동완성합니다.")
    public ResponseEntity<AutoCompleteResponseForm> autoComplete(@RequestParam String keyword) {
        AutoCompleteDto autoCompleteDto = autoCompleteService.autoComplete(keyword);
        return ResponseEntity.status(OK).body(AutoCompleteResponseForm.of(autoCompleteDto));
    }

    @PostMapping
    @ApiOperation(value = "검색어 자동완성단어 임의삽입",
            notes = "검색어로 사용될 단어를 직접 삽입합니다.(식당회원가입시엔 자동삽입되므로 해당 api호출 불 필요)")
    public ResponseEntity<Void> addAutoCompleteWord(@RequestParam String word) {
        autoCompleteService.addAutoCompleteWord(word);
        return ResponseEntity.status(CREATED).build();
    }
}