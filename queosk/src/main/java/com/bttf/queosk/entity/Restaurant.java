package com.bttf.queosk.entity;

import com.bttf.queosk.config.BaseTimeEntity;
import com.bttf.queosk.dto.OperationStatus;
import com.bttf.queosk.dto.RestaurantCategory;
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
}