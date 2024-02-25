package me.castleuk.springbootdeveloperblog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import me.castleuk.springbootdeveloperblog.config.jwt.JwtFactory;
import me.castleuk.springbootdeveloperblog.config.jwt.JwtProperties;
import me.castleuk.springbootdeveloperblog.domain.RefreshToken;
import me.castleuk.springbootdeveloperblog.domain.User;
import me.castleuk.springbootdeveloperblog.dto.CreateAccessTokenRequest;
import me.castleuk.springbootdeveloperblog.repository.RefreshTokenRepository;
import me.castleuk.springbootdeveloperblog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 엑세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        // Given
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                                                .email("user@gmail.com")
                                                .password("test")
                                                .build());

        String refreshToken = JwtFactory.builder()
                                        .claims(Map.of("id", testUser.getId()))
                                        .build()
                                        .createToken(jwtProperties);
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        final String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions resultActions = mockMvc.perform(
            post(url).contentType(MediaType.APPLICATION_JSON_VALUE)
                     .content(requestBody));

        // Then
        resultActions.andExpect(status().isCreated())
                     .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

}
