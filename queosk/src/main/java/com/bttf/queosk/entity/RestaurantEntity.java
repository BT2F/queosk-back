package com.bttf.queosk.entity;

import com.bttf.queosk.config.BaseTimeEntity;
import com.bttf.queosk.domain.enumerate.OperationStatus;
import com.bttf.queosk.domain.enumerate.RestaurantCategory;
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
public class RestaurantEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    private double latitude;

    private double longitude;

    private double ratingAverage;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;

    private long maxWaiting;
}
