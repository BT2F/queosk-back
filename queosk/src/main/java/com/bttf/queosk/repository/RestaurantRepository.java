package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Boolean existsByEmail(String email);
    Optional<Restaurant> findByOwnerId(String ownerId);
}
