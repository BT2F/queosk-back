package com.bttf.queosk.service;

import com.bttf.queosk.domain.RestaurantSignInForm;

public interface RestaurantService {
    void signIn(RestaurantSignInForm restaurantSignInForm) throws Exception;
}
