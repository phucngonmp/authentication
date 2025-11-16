package org.example.sang_garden.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sang_garden.entity.RefreshToken;
import org.example.sang_garden.entity.User;
import org.example.sang_garden.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
        return refreshTokenRepository.save(refreshToken).getId().toString();
    }


    private RefreshToken getRefreshTokenObject(UUID id) {
        return refreshTokenRepository.findById(id).orElse(null);
    }

    public boolean isValidRefreshToken(UUID tokenId) {
        RefreshToken refreshToken = getRefreshTokenObject(tokenId);
        boolean isValid = refreshToken != null && !isExpiredOrRevoked(refreshToken);
        logger.info("Refresh token is valid ? {}", isValid);
        return isValid;
    }

    private boolean isExpiredOrRevoked(RefreshToken refreshToken) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = refreshToken.getExpiresAt();
        return expiresAt.isBefore(now) && refreshToken.isRevoked();
    }

    public User getUser(UUID tokenId) {
        return isValidRefreshToken(tokenId) ? getRefreshTokenObject(tokenId).getUser() : null;
    }

    public void invalidateRefreshToken(UUID refreshTokenUUID) throws Exception {
        refreshTokenRepository.deleteById(refreshTokenUUID);
    }
}
