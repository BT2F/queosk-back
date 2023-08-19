package com.bttf.queosk.entity;


import com.bttf.queosk.dto.restaurantdto.UpdateRestorantInfoForm;
import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.RestaurantCategory;
import com.bttf.queosk.model.usermodel.UserRole;
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

    public void updateRestaurantInfo(UpdateRestorantInfoForm updateRestorantInfoForm) {
        this.ownerId = updateRestorantInfoForm.getOwnerId();
        this.ownerName = updateRestorantInfoForm.getOwnerName();
        this.email = updateRestorantInfoForm.getEmail();
        this.phone = updateRestorantInfoForm.getPhone();
        this.restaurantName = updateRestorantInfoForm.getRestaurantName();
        this.restaurantPhone = updateRestorantInfoForm.getRestaurantPhone();
        this.category = updateRestorantInfoForm.getCategory();
        this.address = updateRestorantInfoForm.getAddress();
        this.maxWaiting = updateRestorantInfoForm.getMaxWaiting();
    }

    public void setGeoPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
