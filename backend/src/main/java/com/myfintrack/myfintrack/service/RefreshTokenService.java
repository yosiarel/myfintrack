package com.myfintrack.myfintrack.service;

import com.myfintrack.myfintrack.entity.RefreshToken;
import com.myfintrack.myfintrack.repository.RefreshTokenRepository;
import com.myfintrack.myfintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findById(userId).orElseThrow())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            refreshTokenRepository.deleteByUser(user);
        });
    }
}
