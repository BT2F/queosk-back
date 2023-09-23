package com.bttf.queosk.service;

import com.bttf.queosk.dto.MenuCreationRequest;
import com.bttf.queosk.dto.MenuDto;
import com.bttf.queosk.dto.MenuStatusRequest;
import com.bttf.queosk.dto.MenuUpdateRequest;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.MENU_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    @Transactional
    public void createMenu(Long restaurantId, MenuCreationRequest menuCreationRequest) {
        menuRepository.save(Menu.of(restaurantId, menuCreationRequest));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "menuList", key = "'restaurantId:' + #restaurantId")
    public List<MenuDto> getMenu(Long restaurantId) {

        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);

        if (menus.isEmpty()) {
            throw new CustomException(MENU_NOT_FOUND);
        }

        return menus.stream()
                .map(MenuDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "menuList", key = "'restaurantId:' + #restaurantId")
    public void updateMenuInfo(Long restaurantId,
                               Long menuId,
                               MenuUpdateRequest menuUpdateRequest) {

        Menu menu = menuRepository.findByIdAndRestaurantId(menuId, restaurantId)
                .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

        menu.setName(menuUpdateRequest.getName());
        menu.setPrice(menuUpdateRequest.getPrice());

        menuRepository.save(menu);
    }

    @Transactional
    @CacheEvict(value = "menuList", key = "'restaurantId:' + #restaurantId")
    public void updateMenuStatus(Long restaurantId,
                                 Long menuId,
                                 MenuStatusRequest menuStatusRequest) {

        Menu menu = menuRepository.findByIdAndRestaurantId(menuId, restaurantId)
                .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

        menu.setStatus(menuStatusRequest.getStatus());

        menuRepository.save(menu);
    }

    @Transactional
    @CacheEvict(value = "menuList", key = "'restaurantId:' + #restaurantId")
    public void updateImage(Long restaurantId, Long menuId, String url) {

        Menu menu = menuRepository.findByIdAndRestaurantId(menuId, restaurantId)
                .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

        menu.setImageUrl(url);

        menuRepository.save(menu);
    }

    @Transactional
    @CacheEvict(value = "menuList", key = "'restaurantId:' + #restaurantId")
    public void deleteMenu(Long restaurantId, Long menuId) {

        Menu menu = menuRepository.findByIdAndRestaurantId(menuId, restaurantId)
                .orElseThrow(() -> new CustomException(MENU_NOT_FOUND));

        menuRepository.delete(menu);
    }
}
