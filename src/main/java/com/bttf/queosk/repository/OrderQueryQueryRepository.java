package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderQueryQueryRepository extends JpaRepository<Order, Long>, SettlementQueryRepository {
}
