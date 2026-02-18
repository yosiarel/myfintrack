package com.myfintrack.myfintrack.controller;

import com.myfintrack.myfintrack.dto.request.LoginRequest;
import com.myfintrack.myfintrack.dto.request.RegisterRequest;
import com.myfintrack.myfintrack.dto.response.ApiResponse;
import com.myfintrack.myfintrack.dto.response.AuthResponse;
import com.myfintrack.myfintrack.service.AuthenticationService;
import com.myfintrack.myfintrack.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.ok(ApiResponse
                .success("User registered successfully. Please check your email to verify your account.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authenticationService.authenticate(request);

        if (authResponse.getRefreshToken() != null && !authResponse.getRefreshToken().isEmpty()) {
            Cookie refreshCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); 
            response.addCookie(refreshCookie);
        }

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(403).body(ApiResponse.error("Refresh token is missing", null));
        }


        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(com.myfintrack.myfintrack.entity.RefreshToken::getUser)
                .map(user -> {
                    return ResponseEntity.status(501)
                            .body(ApiResponse.<AuthResponse>error("Not implemented fully yet", null));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

    }

}
