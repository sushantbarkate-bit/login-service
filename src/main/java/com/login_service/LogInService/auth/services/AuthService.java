package com.login_service.LogInService.auth.services;

import com.login_service.LogInService.auth.entities.User;
import com.login_service.LogInService.auth.payload.AuthResponse;
import com.login_service.LogInService.auth.repository.UserRepository;
import com.login_service.LogInService.auth.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       UserRepository userRepository,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(String username, String password) {

        // Authenticate user credentials

        System.out.println("user name " + username + " password is " + password);
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password)
        );

        // Generate tokens
        String accessToken = jwtUtil.generateToken(username, new HashMap<>()); // optionally add claims
        var refreshToken = refreshTokenService.createRefreshToken(user);

        // Return auth response
        return new AuthResponse(accessToken, refreshToken.getToken(), user.getId());
    }
}
