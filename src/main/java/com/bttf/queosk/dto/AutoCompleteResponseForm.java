package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "검색어 자동완성 Response")
public class AutoCompleteResponseForm {
    private List<String> restaurants;

    public static AutoCompleteResponseForm of(AutoCompleteDto autoCompleteDto){
        return AutoCompleteResponseForm.builder()
                .restaurants(autoCompleteDto.getRestaurants())
                .build();
    }
}
