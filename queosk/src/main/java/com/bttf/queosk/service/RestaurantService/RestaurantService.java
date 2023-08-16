package com.bttf.queosk.service.RestaurantService;

import com.bttf.queosk.dto.restaurantDto.RestaurantSignInForm;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.mapper.RestaurantSignInMapper;
import com.bttf.queosk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void signIn(RestaurantSignInForm restaurantSignInForm) throws Exception {
        if (restaurantRepository.existsByEmail(restaurantSignInForm.getEmail())) {
            throw new Exception(); // TODO : 이메일 중복 익셉션 제작
        }
        Restaurant restaurant =
                RestaurantSignInMapper.MAPPER.toEntity(restaurantSignInForm);
        restaurantRepository.save(restaurant);
    }
}
