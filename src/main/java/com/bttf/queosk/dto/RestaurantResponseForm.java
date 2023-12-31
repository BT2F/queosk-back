package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.RestaurantCategory;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel(value = "매장정보 Response")
public class RestaurantResponseForm {
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

    public static RestaurantResponseForm of(RestaurantDto restaurantDto) {
        return RestaurantResponseForm.builder()
                .id(restaurantDto.getId())
                .ownerId(restaurantDto.getOwnerId())
                .ownerName(restaurantDto.getOwnerName())
                .email(restaurantDto.getEmail())
                .phone(restaurantDto.getPhone())
                .restaurantName(restaurantDto.getRestaurantName())
                .restaurantPhone(restaurantDto.getRestaurantPhone())
                .category(restaurantDto.getCategory())
                .businessNumber(restaurantDto.getBusinessNumber())
                .businessStartDate(restaurantDto.getBusinessStartDate())
                .address(restaurantDto.getAddress())
                .ratingAverage(restaurantDto.getRatingAverage())
                .imageUrl(restaurantDto.getImageUrl())
                .operationStatus(restaurantDto.getOperationStatus())
                .maxWaiting(restaurantDto.getMaxWaiting())
                .region(restaurantDto.getRegion())
                .build();
    }
}

