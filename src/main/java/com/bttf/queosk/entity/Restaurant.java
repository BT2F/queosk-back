package com.bttf.queosk.entity;


import com.bttf.queosk.dto.UpdateRestaurantInfoForm;
import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.RestaurantCategory;
import com.bttf.queosk.enumerate.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "restaurant")
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cid;

    private String ownerId;

    private String ownerName;

    private String password;

    private String email;

    private String phone;

    private String restaurantName;

    private String restaurantPhone;

    @Enumerated(EnumType.STRING)
    private RestaurantCategory category;

    private String businessNumber;

    private Date businessStartDate;

    private String address;

    private Double latitude;

    private Double longitude;

    private String region;

    private Double ratingAverage;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;

    private Long maxWaiting;

    private Boolean isDeleted;

    private UserRole userRole;

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateRestaurantInfo(UpdateRestaurantInfoForm updateRestaurantInfoForm) {
        this.ownerId = updateRestaurantInfoForm.getOwnerId();
        this.cid = updateRestaurantInfoForm.getCid();
        this.ownerName = updateRestaurantInfoForm.getOwnerName();
        this.email = updateRestaurantInfoForm.getEmail();
        this.phone = updateRestaurantInfoForm.getPhone();
        this.restaurantName = updateRestaurantInfoForm.getRestaurantName();
        this.restaurantPhone = updateRestaurantInfoForm.getRestaurantPhone();
        this.category = updateRestaurantInfoForm.getCategory();
        this.address = updateRestaurantInfoForm.getAddress();
        this.maxWaiting = updateRestaurantInfoForm.getMaxWaiting();
    }

    public void setGeoPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
