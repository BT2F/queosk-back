package com.bttf.queosk.repository;

import com.bttf.queosk.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
    Boolean existsByEmail(String email);
}
