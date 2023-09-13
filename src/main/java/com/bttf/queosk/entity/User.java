package com.bttf.queosk.entity;

import com.bttf.queosk.dto.UserSignUpForm;
import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import com.bttf.queosk.enumerate.LoginType;
import com.bttf.queosk.enumerate.UserRole;
import com.bttf.queosk.enumerate.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

import static com.bttf.queosk.enumerate.LoginType.KAKAO;
import static com.bttf.queosk.enumerate.LoginType.NORMAL;
import static com.bttf.queosk.enumerate.UserRole.ROLE_USER;
import static com.bttf.queosk.enumerate.UserStatus.NOT_VERIFIED;
import static com.bttf.queosk.enumerate.UserStatus.VERIFIED;

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

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public static User of(UserSignUpForm.Request userSignUpRequest,
                          String encryptedPassword,
                          String trimmedPhoneNumber) {

        return User.builder()
                .email(userSignUpRequest.getEmail())
                .nickName(userSignUpRequest.getNickName())
                .password(encryptedPassword)
                .phone(trimmedPhoneNumber)
                .loginType(NORMAL)
                .status(NOT_VERIFIED)
                .userRole(ROLE_USER)
                .build();
    }

    public static User of(String email, String nickName, String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickName(nickName)
                .loginType(KAKAO)
                .phone("01000000000") // 임시 조치
                .userRole(ROLE_USER)
                .status(VERIFIED)
                .build();
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.status = userStatus;
    }
}
