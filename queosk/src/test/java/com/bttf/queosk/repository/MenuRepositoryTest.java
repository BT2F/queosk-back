package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.entity.MenuEntity;
import com.bttf.queosk.entity.RestaurantEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
class MenuRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    @DisplayName("메뉴가 DB에 정상적으로 저장된다.")
    public void menuRepository_test() throws Exception {
        // given

        RestaurantEntity savedRestaurant =
                restaurantRepository.save(RestaurantEntity.builder()
                        .id(1L)
                        .ownerId("test")
                        .name("test")
                        .password("asd")
                        .build());

        MenuEntity menu = MenuEntity.builder()
                .id(1L)
                .name("test Menu")
                .price(20000L)
                .restaurant(savedRestaurant)
                .build();
        // when

        MenuEntity savedMenu = menuRepository.save(menu);

        // then

        assertThat(savedMenu.getName()).isEqualTo(menu.getName());
        assertThat(savedMenu.getPrice()).isEqualTo(20000L);
    }

    @Test
    public void loadMenu_test() throws Exception {
        // given
        RestaurantEntity savedRestaurant =
                restaurantRepository.save(RestaurantEntity.builder()
                        .id(1L)
                        .ownerId("test")
                        .name("test")
                        .password("asd")
                        .build());

        MenuEntity menu = MenuEntity.builder()
                .id(1L)
                .name("test Menu")
                .price(20000L)
                .restaurant(savedRestaurant)
                .build();

        menuRepository.save(menu);

        // when
        MenuEntity findMenu = menuRepository.findById(menu.getId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴 ID를 찾을 수 없습니다."));

        // then

        assertThat(findMenu.getName()).isEqualTo(menu.getName());
        assertThat(findMenu.getPrice()).isEqualTo(menu.getPrice());
    }

}