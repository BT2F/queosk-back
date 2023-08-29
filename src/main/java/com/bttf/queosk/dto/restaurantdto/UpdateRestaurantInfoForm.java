package com.bttf.queosk.dto.restaurantdto;

import com.bttf.queosk.enumerate.RestaurantCategory;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRestaurantInfoForm {

    private String ownerId;

    private String cid;

    private String ownerName;

    private String email;

    private String phone;

    private String restaurantName;

    private String restaurantPhone;

    private RestaurantCategory category;

    private String address;

    private Long maxWaiting;
}
