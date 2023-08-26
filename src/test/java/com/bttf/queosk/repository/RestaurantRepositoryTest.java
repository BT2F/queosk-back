package com.bttf.queosk.repository;

import com.bttf.queosk.entity.baseentity.JpaAuditingConfiguration;
import com.bttf.queosk.entity.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @DisplayName("매장이 DB에 저장되는지 확인한다.")
    public void signInRestaurant_test() throws Exception {
        // given
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .ownerId("test")
                .ownerName("test")
                .password("asd")
                .build();

        // when
        Restaurant savedRestaurant =
                restaurantRepository.save(restaurant);

        // then
        assertThat(savedRestaurant.getOwnerId()).isSameAs(restaurant.getOwnerId());
        assertThat(savedRestaurant.getOwnerName()).isSameAs(restaurant.getOwnerName());
    }

    @Test
    @DisplayName("DB에 저장된 매장을 불러올 수 있는지 확인한다.")
    public void findRestorant_test() throws Exception {
        // given
        Restaurant restaurant = restaurantRepository
                .save(
                        Restaurant.builder()
                                .id(1L)
                                .ownerId("test")
                                .ownerName("test")
                                .password("asd")
                                .build()
                );

        // when
        Restaurant findRestaurant = restaurantRepository
                .findById(restaurant.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("잘못된 ID" + restaurant.getId()));
        // then

        assertThat(restaurantRepository.count()).isEqualTo(1);
        assertThat(findRestaurant.getId()).isEqualTo(1L);
        assertThat(findRestaurant.getOwnerId()).isEqualTo("test");
    }
}