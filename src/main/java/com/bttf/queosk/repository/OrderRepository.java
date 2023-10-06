package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Settlement;
import com.bttf.queosk.enumerate.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurantIdAndCreatedAtBetween(Long restaurantId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Order> findAllByRestaurantIdAndStatus(Long restaurantId, OrderStatus orderStatus);

    List<Order> findByUserIdAndStatusNotOrderByCreatedAtDesc(Long userId, OrderStatus orderStatus);

    @Query("SELECT o FROM order o WHERE o.restaurantId = :restaurantId AND o.createdAt >= :fromDate AND o.createdAt <= :toDate")
    List<Order> findOrderByRestaurantInDateRange(
            @Param("restaurantId") Long restaurantId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
