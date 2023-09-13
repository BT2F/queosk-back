package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Order;
import com.bttf.queosk.enumerate.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurantIdAndCreatedAtBetween(Long restaurantId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Order> findAllByRestaurantIdAndStatus(Long restaurantId, OrderStatus orderStatus);
}
