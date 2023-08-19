package com.bttf.queosk.service.menuservice;

import com.bttf.queosk.dto.menudto.CreateMenuForm;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.mapper.CreateMenuMapper;
import com.bttf.queosk.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public void createMenu(CreateMenuForm createMenuForm) {
        Menu menu = CreateMenuMapper.MAPPER.toEntity(createMenuForm);
        menuRepository.save(menu);
    }
}
