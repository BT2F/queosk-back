package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.enumerate.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurantAndCreatedAtBetween(Restaurant restaurant, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Order> findAllByRestaurantAndStatus(OrderStatus orderStatus, Restaurant restaurant);
}
