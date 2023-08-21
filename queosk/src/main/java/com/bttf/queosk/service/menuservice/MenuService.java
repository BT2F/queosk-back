package com.bttf.queosk.service.menuservice;

import com.bttf.queosk.dto.menudto.MenuCreationForm;
import com.bttf.queosk.dto.menudto.MenuDto;
import com.bttf.queosk.dto.menudto.MenuUpdateForm;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.mapper.menumapper.MenuDtoMapper;
import com.bttf.queosk.mapper.menumapper.MenuMapper;
import com.bttf.queosk.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.bttf.queosk.exception.ErrorCode.MENU_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    @Transactional
    public void createMenu(Long restaurantId, MenuCreationForm menuCreationForm) {
        menuRepository.save(MenuMapper.INSTANCE.MenuCreationFormToMenu(restaurantId, menuCreationForm));
    }

    public List<MenuDto> getMenu(Long restaurantId) {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        if(menus.isEmpty()){
            throw new CustomException(MENU_NOT_FOUND);
        }
        List<MenuDto> menuDtos = new ArrayList<>();
        menus.forEach(menu -> {
            menuDtos.add(MenuDtoMapper.INSTANCE.CreateMenuToMenu(menu));
        });

        return menuDtos;
    }

    @Transactional
    public void updateMenuInfo(Long restaurantId, Long menuId, MenuUpdateForm menuUpdateForm) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()->new CustomException(MENU_NOT_FOUND));
        if(!menu.getRestaurantId().equals(restaurantId)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_SERVICE);
        }

        menu.setName(menuUpdateForm.getName());
        menu.setPrice(menuUpdateForm.getPrice());

        menuRepository.save(menu);
    }
}
