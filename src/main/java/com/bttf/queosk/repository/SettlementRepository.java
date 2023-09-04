package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    @Query("SELECT s FROM Settlement s WHERE s.restaurantId = :restaurantId AND s.createdAt >= :fromDate AND s.createdAt <= :toDate")
    List<Settlement> findSettlementsByRestaurantInDateRange(
            @Param("restaurantId") Long restaurantId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
