package com.bttf.queosk.dto;

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
@ApiModel(value = "매장정보 수정 Request")
public class RestaurantUpdateRequestForm {
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
    private OperationStatus operationStatus;
}
