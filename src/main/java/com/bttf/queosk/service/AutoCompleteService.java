package com.bttf.queosk.service;

import com.bttf.queosk.common.AutoCompleteTrie;
import com.bttf.queosk.dto.AutoCompleteDto;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutoCompleteService {

    private final AutoCompleteTrie autoCompleteTrie;

    private final RestaurantRepository restaurantRepository;

    @PostConstruct
    public void initTrieWithRestaurantNames() {
        // 서버 구동 시 한번실행. 가입된 모든 매장의 리스트를 조회하고 AutoCompleteTrie 에 추가
        List<Restaurant> restaurants = restaurantRepository.findByIsDeleted(false);
        restaurants.forEach(restaurant -> {
            autoCompleteTrie.insert(restaurant.getRestaurantName());
        });
        log.info("Auto-Complete Registration Completed (List)");
    }

    // 검색어를 Trie에 추가하는 메서드
    public void saveKeyword(String keyword) {
        // Trie에 검색어 추가
        autoCompleteTrie.insert(keyword);
        log.info("Auto-Complete Registration Completed (Single)");
    }

    // 검색어 자동완성 기능을 위한 메서드
    public AutoCompleteDto autoComplete(String prefix) {
        return AutoCompleteDto.builder().restaurants(autoCompleteTrie.autoComplete(prefix)).build();
    }

    // 탈퇴 시 자동검색어완성에서 해당 식당이름을 지우는 메서드
    public void deleteRestaurantName(Long restaurantId) {
        restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
            autoCompleteTrie.delete(restaurant.getRestaurantName());
        });
    }
}
