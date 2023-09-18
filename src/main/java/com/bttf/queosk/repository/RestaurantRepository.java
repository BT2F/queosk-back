package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Restaurant;
import com.google.common.io.Files;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Boolean existsByEmail(String email);

    Optional<Restaurant> findByOwnerId(String ownerId);

    Optional<Restaurant> findByEmail(String email);
}
