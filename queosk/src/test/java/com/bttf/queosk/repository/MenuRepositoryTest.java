package com.bttf.queosk.repository;

import com.bttf.queosk.entity.baseentity.JpaAuditingConfiguration;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Restaurant;
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

        Restaurant savedRestaurant =
                restaurantRepository.save(Restaurant.builder()
                        .id(1L)
                        .ownerId("test")
                        .ownerName("test")
                        .password("asd")
                        .build());

        Menu menu = Menu.builder()
                .id(1L)
                .name("test Menu")
                .price(20000L)
                .restaurantId(1L)
                .build();
        // when

        Menu savedMenu = menuRepository.save(menu);

        // then

        assertThat(savedMenu.getName()).isEqualTo(menu.getName());
        assertThat(savedMenu.getPrice()).isEqualTo(20000L);
    }

    @Test
    public void loadMenu_test() {
        // given
        Restaurant savedRestaurant =
                restaurantRepository.save(Restaurant.builder()
                        .id(1L)
                        .ownerId("test")
                        .ownerName("test")
                        .password("asd")
                        .build());

        Menu menu = Menu.builder()
                .id(1L)
                .name("test Menu")
                .price(20000L)
                .restaurantId(1L)
                .build();

        menuRepository.save(menu);

        // when
        Menu findMenu = menuRepository.findById(menu.getId()).get();

        // then

        assertThat(findMenu.getName()).isEqualTo(menu.getName());
        assertThat(findMenu.getPrice()).isEqualTo(menu.getPrice());
    }
}