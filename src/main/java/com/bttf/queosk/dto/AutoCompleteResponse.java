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
public class AutoCompleteResponse {
    private List<String> restaurants;

    public static AutoCompleteResponse of(AutoCompleteDto autoCompleteDto){
        return AutoCompleteResponse.builder()
                .restaurants(autoCompleteDto.getRestaurants())
                .build();
    }
}
