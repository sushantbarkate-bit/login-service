package com.login_service.LogInService.auth.payload;
import lombok.*;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
