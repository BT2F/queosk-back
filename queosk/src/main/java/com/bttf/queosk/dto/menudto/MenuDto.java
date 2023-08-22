package com.bttf.queosk.dto.menudto;

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
}
