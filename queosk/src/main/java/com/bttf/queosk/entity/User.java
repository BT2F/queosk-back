package com.bttf.queosk.entity;

import com.bttf.queosk.config.baseEntity.BaseTimeEntity;
import com.bttf.queosk.dto.userDto.UserEditForm;
import com.bttf.queosk.model.userModel.UserRole;
import com.bttf.queosk.model.userModel.UserStatus;
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
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;

    private String email;

    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String imageUrl;

    private String loginApi;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public void editInformation(UserEditForm userEditForm) {
        this.nickName =
                userEditForm.getNickName() == null ? this.nickName : userEditForm.getNickName();

        this.phone =
                userEditForm.getPhone() == null ? this.nickName : userEditForm.getPhone();
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
