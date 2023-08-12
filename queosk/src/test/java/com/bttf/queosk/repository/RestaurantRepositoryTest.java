package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.entity.RestaurantEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @DisplayName("매장이 DB에 저장되는지 확인한다.")
    public void signInRestaurant_test() throws Exception {
        // given
        RestaurantEntity restaurant = RestaurantEntity.builder()
                .id(1L)
                .ownerId("test")
                .name("test")
                .password("asd")
                .build();

        // when
        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurant);

        // then
        Assertions.assertThat(savedRestaurant.getOwnerId()).isSameAs(restaurant.getOwnerId());
        Assertions.assertThat(savedRestaurant.getName()).isSameAs(restaurant.getName());
    }

    @Test
    @DisplayName("DB에 저장된 매장을 불러올 수 있는지 확인한다.")
    public void findRestorant_test() throws Exception {
        // given
        RestaurantEntity restaurant = restaurantRepository
                .save(
                        RestaurantEntity.builder()
                                .id(1L)
                                .ownerId("test")
                                .name("test")
                                .password("asd")
                                .build()
                );

        // when
        RestaurantEntity findRestaurant = restaurantRepository
                .findById(restaurant.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("잘못된 ID" + restaurant.getId()));
        // then

        Assertions.assertThat(restaurantRepository.count()).isEqualTo(1);
        Assertions.assertThat(findRestaurant.getId()).isEqualTo(1L);
        Assertions.assertThat(findRestaurant.getOwnerId()).isEqualTo("test");
    }
}