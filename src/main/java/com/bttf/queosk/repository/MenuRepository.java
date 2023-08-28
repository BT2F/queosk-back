package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByRestaurantId(Long restaurantId);

    Optional<Menu> findByIdAndRestaurantId(Long menuId, Long restaurantId);
}
