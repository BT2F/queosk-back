package com.bttf.queosk.service;

import com.bttf.queosk.dto.AutoCompleteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("검색어 자동완성 관련 테스트코드")
public class AutoCompleteServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private AutoCompleteService autoCompleteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("검색어 추가")
    public void testAddSearchTerm() {
        // Given
        String restaurant = "테스트!!";

        // When
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.add(eq("autocomplete"), eq(restaurant), eq(0.0))).thenReturn(true);

        autoCompleteService.addAutoCompleteWord(restaurant);

        // Then
        verify(zSetOperations).add(eq("autocomplete"), eq(restaurant), eq(0.0));
    }

    @Test
    @DisplayName("검색어 삭제")
    public void testDeleteSearchTerm() {
        // Given
        String restaurant = "테스트!!";

        // When
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.remove(eq("autocomplete"), eq(restaurant))).thenReturn(1L);

        autoCompleteService.deleteAutoCompleteWord(restaurant);

        // Then
        verify(zSetOperations).remove(eq("autocomplete"), eq(restaurant));
    }


    @Test
    @DisplayName("검색어 자동완성(일반)")
    public void testSearchKeywords_NormalSearch() {
        // Given
        String input = "테스트";
        String[] keywords = {"테스트1", "테스트2", "기타"};

        Set<ZSetOperations.TypedTuple<String>> rankedKeywords = new LinkedHashSet<>();
        for (String keyword : keywords) {
            ZSetOperations.TypedTuple<String> tuple = mock(ZSetOperations.TypedTuple.class);
            when(tuple.getValue()).thenReturn(keyword);
            when(tuple.getScore()).thenReturn(1.0); // 모든 검색어의 점수 동일하게 설정
            rankedKeywords.add(tuple);
        }

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForZSet().rangeWithScores("autocomplete", 0, -1)).thenReturn(rankedKeywords);

        // When
        AutoCompleteDto autoCompleteDto = autoCompleteService.autoComplete(input);

        // Then
        assertThat(autoCompleteDto.getRestaurants()).containsExactly("테스트1", "테스트2");
    }

    @Test
    @DisplayName("검색어 자동완성(초성)")
    public void testSearchKeywords_ConsonantSearch() {
        // Given
        String input = "ㄱ";

        Set<String> consonantKeywords = new HashSet<>(Arrays.asList("가게", "갈비집", "기타"));

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForZSet().rangeByScore("autocomplete", 0, 0))
                .thenReturn(consonantKeywords);

        // When
        AutoCompleteDto autoCompleteDto = autoCompleteService.autoComplete(input);

        // Then
        assertThat(autoCompleteDto.getRestaurants())
                .containsExactly("기타", "갈비집", "가게");
    }
}