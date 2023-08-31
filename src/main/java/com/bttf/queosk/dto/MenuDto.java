package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.enumerate.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuDto {
    private Long id;
    private Long restaurantId;
    private String name;
    private String imageUrl;
    private Long price;
    private MenuStatus status;

    public static MenuDto of(Menu menu) {
        return MenuDto.builder()
                .id(menu.getId())
                .restaurantId(menu.getRestaurantId())
                .name(menu.getName())
                .imageUrl(menu.getImageUrl())
                .price(menu.getPrice())
                .status(menu.getStatus())
                .build();
    }
}
