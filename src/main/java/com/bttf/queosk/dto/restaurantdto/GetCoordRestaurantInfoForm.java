package com.bttf.queosk.dto.restaurantdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCoordRestaurantInfoForm {
    private Double x;
    private Double y;
    private int page;
    private int size;
}
