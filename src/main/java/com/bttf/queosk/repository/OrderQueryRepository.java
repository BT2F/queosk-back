package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderQueryRepository extends JpaRepository<Order, Long>, SettlementRepository{
}
