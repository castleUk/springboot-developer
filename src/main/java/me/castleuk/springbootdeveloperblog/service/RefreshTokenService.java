package me.castleuk.springbootdeveloperblog.service;

import lombok.RequiredArgsConstructor;
import me.castleuk.springbootdeveloperblog.domain.RefreshToken;
import me.castleuk.springbootdeveloperblog.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                                     .orElseThrow(
                                         () -> new IllegalArgumentException("Unexpected token"));
    }
}
