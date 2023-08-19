package com.bttf.queosk.dto.tokendto;

import com.bttf.queosk.model.userModel.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {
    private String email;
    private Long id;
    private UserRole userRole;
}
