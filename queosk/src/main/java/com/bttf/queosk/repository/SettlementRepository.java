package com.bttf.queosk.repository;

import com.bttf.queosk.entity.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<SettlementEntity, Long> {
}