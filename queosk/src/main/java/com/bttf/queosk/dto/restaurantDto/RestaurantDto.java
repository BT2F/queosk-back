package com.bttf.queosk.dto.restaurantDto;

import com.bttf.queosk.dto.enumerate.OperationStatus;
import com.bttf.queosk.dto.enumerate.RestaurantCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantDto {
    private String ownerId;

    private String ownerName;

    private String email;

    private String phone;

    private String restaurantName;

    private String restaurantPhone;

    private RestaurantCategory category;

    private String businessNumber;

    private Date businessStartDate;

    private String address;

    private Double ratingAverage;

    private String imageUrl;

    private OperationStatus operationStatus;

    private Long maxWaiting;
}
