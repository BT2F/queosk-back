package com.bttf.queosk.model.userModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public enum UserRole {
    ROLE_USER("ROLE_USER",
            Collections.singletonList(new SimpleGrantedAuthority("USER"))),

    ROLE_ADMIN("ROLE_RESTAURANT",
            Collections.singletonList(new SimpleGrantedAuthority("RESTAURANT")));

    private final String roleName;
    private final List<GrantedAuthority> authorities;
}