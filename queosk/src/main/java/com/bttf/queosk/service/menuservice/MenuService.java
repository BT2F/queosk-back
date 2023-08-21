package com.bttf.queosk.service.menuservice;

import com.bttf.queosk.dto.menudto.CreateMenuForm;
import com.bttf.queosk.dto.menudto.MenuDto;
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

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    @Transactional
    public void createMenu(Long restaurantId, CreateMenuForm createMenuForm) {
        menuRepository.save(MenuMapper.INSTANCE.CreateMenuToMenu(restaurantId, createMenuForm));
    }

    public List<MenuDto> getMenu(Long restaurantId) {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        if(menus.isEmpty()){
            throw new CustomException(ErrorCode.MENU_NOT_FOUND);
        }
        List<MenuDto> menuDtos = new ArrayList<>();
        menus.forEach(menu -> {
            menuDtos.add(MenuDtoMapper.INSTANCE.CreateMenuToMenu(menu));
        });

        return menuDtos;
    }
}
