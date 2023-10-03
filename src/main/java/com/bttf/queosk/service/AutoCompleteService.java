package com.bttf.queosk.service;

import com.bttf.queosk.dto.AutoCompleteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoCompleteService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String AUTOCOMPLETE_KEY = "autocomplete";

    private final String[] chs = {
            "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ",
            "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};

    //검색어 등록
    public void addAutoCompleteWord(String restaurant) {
        redisTemplate.opsForZSet().add(AUTOCOMPLETE_KEY, restaurant, 0);
    }

    //등록된 검색어 삭제
    public void deleteAutoCompleteWord(String restaurant) {
        redisTemplate.opsForZSet().remove(AUTOCOMPLETE_KEY, restaurant);
    }

    public AutoCompleteDto autoComplete(String input) {

        List<String> matchingKeywords = new ArrayList<>(
                isKoreanConsonant(input.charAt(0)) ?
                        searchByConsonant(input) :
                        searchByGeneral(input)
        );

        return AutoCompleteDto.builder()
                .restaurants(matchingKeywords)
                .build();
    }


    private List<String> searchByConsonant(String input) {
        List<String> matchingKeywords = new ArrayList<>();
        Set<String> consonantKeywords = redisTemplate.opsForZSet().rangeByScore(AUTOCOMPLETE_KEY, 0, 0);

        consonantKeywords.forEach(restaurant -> {
            String inputTrimmed = removeKoreanCharacters(input).trim();
            if (extractConsonant(restaurant).contains(inputTrimmed)) {
                matchingKeywords.add(restaurant);
            }
        });

        return matchingKeywords;
    }

    private List<String> searchByGeneral(String input) {
        List<String> matchingKeywords = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> rankedKeywords = redisTemplate.opsForZSet().rangeWithScores(AUTOCOMPLETE_KEY, 0, -1);

        if (rankedKeywords != null && !rankedKeywords.isEmpty()) {
            List<String> sortedKeywords = rankedKeywords.stream()
                    .sorted((a, b) -> Double.compare(b.getScore(), a.getScore())) // 내림차순 정렬
                    .map(ZSetOperations.TypedTuple::getValue)
                    .collect(Collectors.toList());

            String trimmedInput = removeConsonantsAndVowelsCharacters(input).trim();
            sortedKeywords.forEach(keyword -> {
                if (keyword.contains(trimmedInput)) {
                    matchingKeywords.add(keyword);
                }
            });
        }

        return matchingKeywords;
    }

    //문자에서 초성을 추출
    private String extractConsonant(String text) {
        StringBuilder chosung = new StringBuilder();

        text.chars().forEach(unicode -> {
            if (isKoreanCharacter((char) unicode)) {
                int chosungIndex = (unicode - '가') / (28 * 21);
                chosung.append(chs[chosungIndex]);
            }
        });

        return chosung.toString();
    }

    //한글 문자인지 확인
    private boolean isKoreanCharacter(char ch) {
        return ch >= 0xAC00 && ch <= 0xD7A3;
    }

    //한글 모음인지 확인
    private boolean isKoreanConsonant(char ch) {
        return ch >= 'ㄱ' && ch <= 'ㅎ';
    }

    //만약 일반조회일 경우 완성되지 않은 한글을 제거
    private String removeConsonantsAndVowelsCharacters(String text) {
        return text.replaceAll("[ㄱ-ㅎㅏ-ㅣ]+", "");
    }

    //만약 초성조회일 경우 완성된 한글을 제거
    private String removeKoreanCharacters(String text) {
        return text.replaceAll("[가-힣]+", "");
    }
}
