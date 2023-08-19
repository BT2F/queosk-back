package com.bttf.queosk.dto.restaurantDto;

import com.bttf.queosk.dto.enumerate.OperationStatus;
import com.bttf.queosk.dto.enumerate.RestaurantCategory;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.service.restaurantService.RestaurantService;
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
    private long id;

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

    public static RestaurantDto of(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .ownerId(restaurant.getOwnerId())
                .ownerName(restaurant.getOwnerName())
                .email(restaurant.getEmail())
                .phone(restaurant.getPhone())
                .restaurantName(restaurant.getRestaurantName())
                .restaurantPhone(restaurant.getRestaurantPhone())
                .category(restaurant.getCategory())
                .businessNumber(restaurant.getBusinessNumber())
                .businessStartDate(restaurant.getBusinessStartDate())
                .address(restaurant.getAddress())
                .ratingAverage(restaurant.getRatingAverage())
                .imageUrl(restaurant.getImageUrl())
                .operationStatus(restaurant.getOperationStatus())
                .maxWaiting(restaurant.getMaxWaiting())
                .build();
    }
}
