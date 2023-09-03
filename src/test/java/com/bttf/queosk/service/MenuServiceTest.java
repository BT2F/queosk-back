package com.bttf.queosk.service;

import com.bttf.queosk.dto.MenuCreationForm;
import com.bttf.queosk.dto.MenuDto;
import com.bttf.queosk.dto.MenuStatusForm;
import com.bttf.queosk.dto.MenuUpdateForm;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bttf.queosk.enumerate.MenuStatus.ON_SALE;
import static com.bttf.queosk.enumerate.MenuStatus.SOLD_OUT;
import static com.bttf.queosk.exception.ErrorCode.MENU_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    @DisplayName("메뉴 생성 테스트 - 성공")
    void testCreateMenu_Success() {
        // given
        Long restaurantId = 1L;

        MenuCreationForm.Request menuCreationForm =
                MenuCreationForm.Request.builder()
                        .name("menu")
                        .price(1000L)
                        .imageUrl(null)
                        .build();
        // when
        menuService.createMenu(restaurantId, menuCreationForm);

        // then
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 목록 조회 테스트 - 성공")
    void testGetMenu_Success() {
        // Given
        Long restaurantId = 1L;
        Menu menu1 =
                Menu.builder()
                        .name("menu1")
                        .price(1000L)
                        .status(ON_SALE)
                        .restaurantId(restaurantId)
                        .build();
        Menu menu2 =
                Menu.builder()
                        .name("menu2")
                        .price(2000L)
                        .status(ON_SALE)
                        .restaurantId(restaurantId)
                        .build();
        List<Menu> menus = Arrays.asList(menu1, menu2);

        when(menuRepository.findByRestaurantId(restaurantId)).thenReturn(menus);

        // When
        List<MenuDto> menuDtos = menuService.getMenu(restaurantId);

        // Then
        assertThat(menuDtos.size()).isEqualTo(2);
        assertThat(menuDtos.get(0).getName()).isEqualTo(menu1.getName());
        assertThat(menuDtos.get(0).getPrice()).isEqualTo(menu1.getPrice());
        assertThat(menuDtos.get(1).getName()).isEqualTo(menu2.getName());
        assertThat(menuDtos.get(1).getPrice()).isEqualTo(menu2.getPrice());
    }

    @Test
    @DisplayName("메뉴 목록 조회 테스트 - 실패(메뉴 없음)")
    void testGetMenu_MenuNotFound() {
        // Given
        Long restaurantId = 1L;
        when(menuRepository.findByRestaurantId(restaurantId)).thenReturn(new ArrayList<>());

        // When & Then
        assertThatThrownBy(() -> menuService.getMenu(restaurantId))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("메뉴 정보 업데이트 테스트 - 성공")
    void testUpdateMenuInfo_Success() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        MenuUpdateForm.Request menuUpdateForm =
                MenuUpdateForm.Request.builder()
                        .name("updatedMenu")
                        .price(1500L)
                        .build();
        Menu existingMenu =
                Menu.builder()
                        .id(menuId)
                        .name("originalMenu")
                        .price(1000L)
                        .status(ON_SALE)
                        .restaurantId(restaurantId)
                        .build();

        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.of(existingMenu));

        // When
        menuService.updateMenuInfo(restaurantId, menuId, menuUpdateForm);

        // Then
        verify(menuRepository, times(1)).save(existingMenu);
        assertThat(existingMenu.getName()).isEqualTo(menuUpdateForm.getName());
        assertThat(existingMenu.getPrice()).isEqualTo(menuUpdateForm.getPrice());
    }

    @Test
    @DisplayName("메뉴 정보 업데이트 테스트 - 실패(메뉴 없음)")
    void testUpdateMenuInfo_MenuNotFound() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        MenuUpdateForm.Request menuUpdateForm =
                MenuUpdateForm.Request.builder()
                        .name("updatedMenu")
                        .price(1500L)
                        .build();
        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThatThrownBy(() -> menuService.updateMenuInfo(restaurantId, menuId, menuUpdateForm))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("메뉴 상태 업데이트 테스트 - 성공")
    void testUpdateMenuStatus_Success() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        MenuStatusForm.Request menuStatusForm =
                MenuStatusForm.Request.builder()
                        .status(SOLD_OUT)
                        .build();
        Menu existingMenu =
                Menu.builder()
                        .id(menuId)
                        .name("menu")
                        .price(1000L)
                        .status(ON_SALE)
                        .restaurantId(restaurantId)
                        .build();

        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.of(existingMenu));

        // When
        menuService.updateMenuStatus(restaurantId, menuId, menuStatusForm);

        // Then
        verify(menuRepository, times(1)).save(existingMenu);
        assertThat(existingMenu.getStatus()).isEqualTo(menuStatusForm.getStatus());
    }

    @Test
    @DisplayName("메뉴 상태 업데이트 테스트 - 실패(메뉴 없음)")
    void testUpdateMenuStatus_MenuNotFound() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        MenuStatusForm.Request menuStatusForm =
                MenuStatusForm.Request.builder()
                        .status(SOLD_OUT)
                        .build();
        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThatThrownBy(() -> menuService.updateMenuStatus(restaurantId, menuId, menuStatusForm))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("이미지 업데이트 테스트 - 성공")
    void testUpdateImage_Success() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        String imageUrl = "http://example.com/image.jpg";
        Menu existingMenu =
                Menu.builder()
                        .id(menuId)
                        .name("menu")
                        .price(1000L)
                        .imageUrl(null)
                        .restaurantId(restaurantId)
                        .build();

        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.of(existingMenu));

        // When
        menuService.updateImage(restaurantId, menuId, imageUrl);

        // Then
        verify(menuRepository, times(1)).save(existingMenu);
        assertThat(existingMenu.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("이미지 업데이트 테스트 - 실패(메뉴 없음)")
    void testUpdateImage_MenuNotFound() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        String imageUrl = "http://example.com/image.jpg";
        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThatThrownBy(() -> menuService.updateImage(restaurantId, menuId, imageUrl))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", MENU_NOT_FOUND);
    }

    @Test
    @DisplayName("메뉴 삭제 테스트 - 성공")
    void testDeleteMenu_Success() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        Menu existingMenu =
                Menu.builder()
                        .id(menuId)
                        .name("menu")
                        .price(1000L)
                        .status(ON_SALE)
                        .imageUrl(null)
                        .restaurantId(restaurantId)
                        .build();

        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.of(existingMenu));

        // When
        menuService.deleteMenu(restaurantId, menuId);

        // Then
        verify(menuRepository, times(1)).delete(existingMenu);
    }

    @Test
    @DisplayName("메뉴 삭제 테스트 - 실패(메뉴 없음)")
    void testDeleteMenu_MenuNotFound() {
        // Given
        Long restaurantId = 1L;
        Long menuId = 1L;
        when(menuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThatThrownBy(() -> menuService.deleteMenu(restaurantId, menuId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", MENU_NOT_FOUND);
    }
}
