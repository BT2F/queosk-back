package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findFirstByUserIdAndRestaurantIdOrderByCreatedAtDesc(Long userId, Long restaurantId);

    List<Queue> findByUserId(Long userId);
}
