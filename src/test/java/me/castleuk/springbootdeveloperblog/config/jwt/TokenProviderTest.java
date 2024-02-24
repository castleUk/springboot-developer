package me.castleuk.springbootdeveloperblog.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import me.castleuk.springbootdeveloperblog.domain.User;
import me.castleuk.springbootdeveloperblog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    //generateToken 검증 테스트
    @DisplayName("generateToken() :  유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // Given
        User testUser = userRepository.save(User.builder()
                                            .email("user@gmail.com")
                                            .password("test")
                                            .build());
        // When
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));
        // Then
        Long userId = Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey())
            .parseClaimsJws(token)
            .getBody()
            .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }
    // validToken() 검증 테스트
    @DisplayName("validToken() : 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // Given
        String token = JwtFactory.builder()
                                 .expiration(new Date(new Date().getTime() - Duration.ofDays(7)
                                                                                     .toMillis()))
                                 .build()
                                 .createToken(jwtProperties);
        // When
        boolean result = tokenProvider.validToken(token);
        // Then
        assertThat(result).isFalse();
    }

    @DisplayName("validToken() : 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // Given
        String token = JwtFactory.withDefaultValues()
                                 .createToken(jwtProperties);
        // When
        boolean result = tokenProvider.validToken(token);
        // Then
        assertThat(result).isTrue();
    }

    // getAuthentication() 검증 테스트
    @DisplayName("getAuthentication() : 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // Given
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                                 .subject(userEmail)
                                 .build()
                                 .createToken(jwtProperties);
        // When
        Authentication authentication = tokenProvider.getAuthentication(token);
        // Then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(
            userEmail);
    }

    //getUserId() 검증 테스트
    @DisplayName("getUserId() : 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // Given
        Long userId = 1L;
        String token = JwtFactory.builder()
                                 .claims(Map.of("id", userId))
                                 .build()
                                 .createToken(jwtProperties);
        // When
        Long userIdByToken = tokenProvider.getUserId(token);
        // Then
        assertThat(userIdByToken).isEqualTo(userId);
    }

}
