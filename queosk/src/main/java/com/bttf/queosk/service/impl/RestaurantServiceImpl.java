package com.bttf.queosk.service.impl;

import com.bttf.queosk.domain.RestaurantSignInDto;
import com.bttf.queosk.entity.RestaurantEntity;
import com.bttf.queosk.mapping.RestaurantSignInMapper;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    @Override
    public Object signIn(RestaurantSignInDto restaurantSignInDto) {
        RestaurantEntity restaurant =
                RestaurantSignInMapper.MAPPER.toEntity(restaurantSignInDto);
        restaurantRepository.save(restaurant);
        return restaurant.getId();
    }
}
