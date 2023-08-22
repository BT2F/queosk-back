package com.bttf.queosk.service.userservice;


import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserTokenDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.bttf.queosk.entity.User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        List<GrantedAuthority> authorities = new ArrayList<>(userEntity.getUserRole().getAuthorities());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .build();
    }

    public UserDetails loadRestaurantByUsername(String email) throws UsernameNotFoundException {
        Restaurant restaurant = restaurantRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));

        List<GrantedAuthority> authorities = new ArrayList<>(restaurant.getUserRole().getAuthorities());

        return User.builder()
                .username(restaurant.getEmail())
                .password(restaurant.getPassword())
                .authorities(authorities)
                .build();
    }
}

