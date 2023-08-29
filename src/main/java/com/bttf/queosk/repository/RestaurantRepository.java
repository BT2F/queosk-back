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

    @Query(value = "SELECT *, (6371 * acos(cos(radians(:lat)) * cos(radians(\"latitude\")) * cos(radians(\"longitude\") - radians(:lng)) + sin(radians(:lat)) * sin(radians(\"latitude\")))) AS distance FROM \"restaurant\" WHERE \"region\" = :region ORDER BY distance",
            countQuery = "SELECT count(*) FROM \"restaurant\" r WHERE \"region\" = :region",
            nativeQuery = true)
    Page<Restaurant> getRestaurantListByDistance(
            @Param("region") String region,
            double lat,
            double lng,
            Pageable pageable);
}
