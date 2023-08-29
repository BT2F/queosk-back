package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantAndIsDeletedFalse(Restaurant restaurant);
}
