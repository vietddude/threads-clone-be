package viet.io.threadsbe.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.http.*;
import viet.io.threadsbe.dto.UserDTO;
import viet.io.threadsbe.dto.auth.OauthRequest;
import viet.io.threadsbe.dto.auth.OauthResponse;
import viet.io.threadsbe.dto.auth.TokenResponse;
import viet.io.threadsbe.dto.auth.UserInfo;
import viet.io.threadsbe.dto.request.RefreshTokenRequest;
import viet.io.threadsbe.dto.request.SetupRequest;
import viet.io.threadsbe.dto.response.RefreshTokenResponse;
import viet.io.threadsbe.entity.User;
import viet.io.threadsbe.exception.AuthenticationException;
import viet.io.threadsbe.mapper.UserMapper;
import viet.io.threadsbe.repository.UserRepository;
import viet.io.threadsbe.security.JwtService;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestClient googleRestClient;
    private final RedisService redisService;
    private final JwtService jwtService;

    private TokenResponse getOauthTokens(String code) {
        // Tạo form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("grant_type", "authorization_code");

        // Gọi API Google
        return googleRestClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .body(formData)
                .retrieve()
                .body(TokenResponse.class);
    }

    private UserInfo getUserInfo(String accessToken) {
        return googleRestClient.get()
                .uri("https://www.googleapis.com/oauth2/v1/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(UserInfo.class);
    }

    private User saveOrUpdateUser(UserInfo userInfo) {
        String username = userInfo.getEmail().split("@")[0]; // Consider a more unique identifier
        Optional<User> existingUser = userRepository.findByUsername(username);

        return existingUser.orElseGet(() -> {
            User newUser = UserMapper.INSTANCE.userInfoToUser(userInfo);
            log.info("Mapped new user: {}", newUser);
            return userRepository.save(newUser);
        });
    }

    private void validateTokenResponse(TokenResponse tokenResponse) {
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new AuthenticationException("Failed to retrieve tokens from Google");
        }
    }

    private void validateUserEmail(UserInfo userInfo) {
        if (!userInfo.isEmailVerified()) {
            throw new AuthenticationException("Email is not verified");
        }
    }

    private String generateAndSaveRefreshToken(UUID userId) {
        String refreshToken = jwtService.genRefreshToken(userId);
        String hashedToken = passwordEncoder.encode(refreshToken);
        redisService.set("refresh:" + userId, hashedToken, 7 * 24 * 60 * 60); // 7 days TTL
        return refreshToken;
    }

    // Public methods
    public OauthResponse googleOAuth(OauthRequest request) {
        String code = request.getCode();
        try {
            log.info("Fetching tokens for code: {}", code);
            TokenResponse tokenResponse = getOauthTokens(code);
            validateTokenResponse(tokenResponse);

            log.info("Fetching user info for access token");
            UserInfo userInfo = getUserInfo(tokenResponse.getAccessToken());
            validateUserEmail(userInfo);

            log.info("Saving or updating user");
            User user = saveOrUpdateUser(userInfo);

            log.info("Generating tokens");
            String accessToken = jwtService.genAccessToken(user.getId());
            String refreshToken = generateAndSaveRefreshToken(user.getId());

            return new OauthResponse(
                    UserMapper.INSTANCE.userToUserDTO(user),
                    accessToken,
                    refreshToken
            );
        } catch (Exception e) {
            log.error("Google OAuth error", e);
            throw new AuthenticationException("Google OAuth failed", e);
        }
    }

    public UserDTO setup(UUID authId, SetupRequest request) {
        User user = userRepository.findById(authId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isUpdated = false;

        if (!request.getBio().equals(user.getBio())) {
            user.setBio(request.getBio());
            isUpdated = true;
        }
        if (!request.getLink().equals(user.getLink())) {
            user.setLink(request.getLink());
            isUpdated = true;
        }
        if (!request.getPrivacy().equals(user.getPrivacy())) {
            user.setPrivacy(request.getPrivacy());
            isUpdated = true;
        }
        user.setVerified(true);
        if (isUpdated) {
            userRepository.save(user);
        }

        return UserMapper.INSTANCE.userToUserDTO(user);
    }

    public RefreshTokenResponse refresh(UUID authId, RefreshTokenRequest request) {
        String refreshToken = redisService.get("refresh:" + authId);
        if (refreshToken == null || !refreshToken.equals(request.getRefreshToken())) {
            throw new AuthenticationException("Invalid refresh token");
        }

        String accessToken = jwtService.genAccessToken(authId);
        String newRefreshToken = jwtService.genRefreshToken(authId);
        redisService.set("refresh:" + authId, newRefreshToken);

        return new RefreshTokenResponse(accessToken, newRefreshToken);
    }
}

