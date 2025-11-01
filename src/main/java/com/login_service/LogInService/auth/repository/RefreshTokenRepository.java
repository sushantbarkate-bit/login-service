package com.login_service.LogInService.auth.repository;

import com.login_service.LogInService.auth.entities.RefreshToken;
import com.login_service.LogInService.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
