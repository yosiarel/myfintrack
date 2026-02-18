package com.myfintrack.myfintrack.service;

import com.myfintrack.myfintrack.dto.request.LoginRequest;
import com.myfintrack.myfintrack.dto.request.RegisterRequest;
import com.myfintrack.myfintrack.dto.response.AuthResponse;
import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .failedAttempts(0)
                .lockTime(null)
                .build();

        User savedUser = userRepository.save(user); // Saved with enabled=true
        log.info("User registered successfully: {}", savedUser.getEmail());

        var jwtToken = jwtService.generateToken(new UserDetailsImpl(savedUser));
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .build();
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isAccountNonLocked()) {
            log.warn("Login attempt for locked account: {}", request.getEmail());
            throw new LockedException("Account is locked due to too many failed attempts. Try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            log.info("User authenticated successfully: {}", request.getEmail());

            if (user.getFailedAttempts() > 0) {
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            }

        } catch (BadCredentialsException e) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= 5) {
                user.setLockTime(LocalDateTime.now());
                userRepository.save(user);
                log.warn("Account locked for user: {} due to too many failed attempts", request.getEmail());
                throw new LockedException("Account locked due to 5 failed attempts.");
            }

            userRepository.save(user);
            throw e;
        }

        var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
