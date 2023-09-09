package com.bttf.queosk.entity;

import com.bttf.queosk.dto.MenuCreationForm;
import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import com.bttf.queosk.enumerate.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

import static com.bttf.queosk.enumerate.MenuStatus.ON_SALE;

@Entity(name = "menu")
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long restaurantId;

    private String name;

    private String imageUrl;

    private Long price;

    @Enumerated(EnumType.STRING)
    private MenuStatus status;

    public static Menu of(Long restaurantId, MenuCreationForm.Request menuCreationRequest) {
        return Menu.builder()
                .name(menuCreationRequest.getName())
                .price(menuCreationRequest.getPrice())
                .status(ON_SALE)
                .imageUrl(menuCreationRequest.getImageUrl())
                .restaurantId(restaurantId)
                .build();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setStatus(MenuStatus status) {
        this.status = status;
    }
}
