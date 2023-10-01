package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.RestaurantCategory;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Restaurant Dto")
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
    private String region;

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
                .region(restaurant.getRegion())
                .build();
    }
}
