package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Restaurant;
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

    @Query(value = "SELECT *, (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(latitude)))) AS distance\n" +
            "FROM restaurant\n" +
            "WHERE category = CASE WHEN :category = 'ALL' THEN category ELSE :category END\n" +
            "ORDER BY distance",
            countQuery = "SELECT COUNT(*) FROM restaurant WHERE category = CASE WHEN :category = 'ALL' THEN category ELSE :category END",
            nativeQuery = true)
    Page<Restaurant> getRestaurantListByDistance(
            double lat,
            double lng,
            Pageable pageable,
            @Param("category") String category);
}
