package com.bttf.queosk.entity;

import com.bttf.queosk.config.BaseTimeEntity;
import com.bttf.queosk.domain.enumerate.LoginApi;
import com.bttf.queosk.domain.enumerate.Verified;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity(name = "user")
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String email;

    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private LoginApi loginApi;

    @Enumerated(EnumType.STRING)
    private Verified verified;

    private String imageUrl;
}
