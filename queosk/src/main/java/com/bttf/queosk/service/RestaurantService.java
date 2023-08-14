package com.bttf.queosk.service;

import com.bttf.queosk.domain.RestaurantSignInDto;

public interface RestaurantService {
    Object signIn(RestaurantSignInDto restaurantSignInDto);
}
