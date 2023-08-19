package com.bttf.queosk.repository;

import com.bttf.queosk.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    String findByUserEmail(String email);
}
