package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    List<Queue> findByUserIdAndRestaurantIdOrderByCreatedAtDesc(Long userId, Long restaurantId);
}
