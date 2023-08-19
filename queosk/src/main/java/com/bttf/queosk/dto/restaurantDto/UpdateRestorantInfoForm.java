package com.bttf.queosk.dto.restaurantDto;

import com.bttf.queosk.dto.enumerate.RestaurantCategory;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRestorantInfoForm {

    private String ownerId;

    private String ownerName;

    private String email;

    private String phone;

    private String restaurantName;

    private String restaurantPhone;

    private RestaurantCategory category;

    private String address;

    private Long maxWaiting;
}
