package viet.io.threadsbe.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import viet.io.threadsbe.dto.UserDTO;
import viet.io.threadsbe.dto.auth.OauthRequest;
import viet.io.threadsbe.dto.auth.OauthResponse;
import viet.io.threadsbe.dto.request.RefreshTokenRequest;
import viet.io.threadsbe.dto.request.SetupRequest;
import viet.io.threadsbe.dto.response.RefreshTokenResponse;
import viet.io.threadsbe.security.SecurityUtils;
import viet.io.threadsbe.service.AuthService;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/google/callback")
    public ResponseEntity<OauthResponse> googleOAuth(@RequestBody OauthRequest request) {
        OauthResponse response = authService.googleOAuth(request);
        // Tạo cookie cho refresh token
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                .httpOnly(true) // Bảo mật cookie
                .secure(false)   // Chỉ gửi qua HTTPS
                .path("/")      // Hiệu lực toàn domain
                .maxAge(30 * 24 * 60 * 60) // TTL: 30 ngày
                .build();


        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(new OauthResponse(
                        response.getUser(), // Hoặc trả thêm thông tin user nếu cần
                        response.getAccessToken(),
                        null // Không trả refresh token trong body
                ));
    }

    @PutMapping("/setup")
    public UserDTO setup(@RequestBody @NotNull SetupRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return authService.setup(authId, request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody @NotNull RefreshTokenRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        RefreshTokenResponse tokens = authService.refresh(authId, request);
        return ResponseEntity.ok()
                .header("Set-Cookie", tokens.getRefreshToken())
                .body(new RefreshTokenResponse(
                        tokens.getAccessToken(),
                        null // Không trả refresh token trong body
                ));
    }
}
