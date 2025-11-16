package com.login_service.LogInService.auth.controllers;

import com.login_service.LogInService.auth.entities.User;
import com.login_service.LogInService.auth.payload.*;
import com.login_service.LogInService.auth.repository.*;
import com.login_service.LogInService.auth.security.*;
import com.login_service.LogInService.auth.services.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RefreshTokenService refreshTokenService,
                          RefreshTokenRepository refreshTokenRepository,
                          JwtUtil jwtUtil) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        String email = body.get("email");
//        if (userRepository.findByUsername(username).isPresent()) {
//            return ResponseEntity.badRequest().body("Username already exists");
//        }
        User user = User.builder()
                // .id(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .lastName(lastName)
                .firstName(firstName)
                .email(email)
                .roles(Set.of("ROLE_USER"))
                .build();
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        System.out.println("user name is " + request.getUsername());
        AuthResponse resp = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String requestRefreshToken = body.get("refreshToken");
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(token -> {
                    refreshTokenService.verifyExpiration(token);
                    User user = token.getUser();
                    String accessToken = jwtUtil.generateToken(user.getUsername(), Map.of());
                    return ResponseEntity.ok(new AuthResponse(accessToken, token.getToken(), user.getId()));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            int deleted = refreshTokenService.deleteByUser(userOpt.get());
            return ResponseEntity.ok("Deleted refresh tokens: " + deleted);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> logout(@PathVariable UUID id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(user.get());
        }
    }
}
