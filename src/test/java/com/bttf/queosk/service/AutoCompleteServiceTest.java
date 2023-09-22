package com.bttf.queosk.service;

import com.bttf.queosk.common.AutoCompleteTrie;
import com.bttf.queosk.dto.AutoCompleteDto;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AutoCompleteServiceTest {
    @Mock
    private AutoCompleteTrie autoCompleteTrie;

    @Mock
    private RestaurantRepository restaurantRepository;

    private AutoCompleteService autoCompleteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        autoCompleteService = new AutoCompleteService(autoCompleteTrie, restaurantRepository);
    }

    @Test
    @DisplayName("검색어자동완성 생성 테스트 - 성공")
    public void saveKeywordToTrie() {
        // Given
        String keyword = "TestKeyword";

        // When
        autoCompleteService.saveKeyword(keyword);

        // Then
        verify(autoCompleteTrie, times(1)).insert(keyword);
    }

    @Test
    @DisplayName("검색어자동완성 조회 테스트 - 성공")
    public void returnAutoCompleteList() {
        // Given
        String prefix = "TestPrefix";
        List<String> autoCompleteList = new ArrayList<>();
        autoCompleteList.add("TestRestaurant1");
        autoCompleteList.add("TestRestaurant2");

        when(autoCompleteTrie.autoComplete(prefix)).thenReturn(autoCompleteList);

        // When
        AutoCompleteDto result = autoCompleteService.autoComplete(prefix);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRestaurants()).isEqualTo(autoCompleteList);
    }

    @Test
    @DisplayName("검색어자동완성 삭제 테스트 - 성공")
    public void deleteRestaurantNameFromTrie() {
        // Given
        Long restaurantId = 1L;
        String restaurantName = "TestRestaurant";

        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .restaurantName(restaurantName)
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When
        autoCompleteService.deleteRestaurantName(restaurantId);

        // Then
        verify(autoCompleteTrie, times(1)).delete(restaurantName);
    }
}