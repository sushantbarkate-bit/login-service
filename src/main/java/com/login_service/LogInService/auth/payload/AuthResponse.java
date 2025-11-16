package com.login_service.LogInService.auth.payload;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UUID userId;

    public AuthResponse(String accessToken, String refreshToken, UUID userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer"; // default
        this.userId = userId;
    }
}
