package viet.io.threadsbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "Authentication APIs")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Handle Google OAuth callback", description = "Handles the callback from Google OAuth and returns an OAuth response with access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/google/callback")
    public ResponseEntity<OauthResponse> googleOAuth(@RequestBody OauthRequest request) {
        OauthResponse response = authService.googleOAuth(request);
        // Create a refresh token cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(30 * 24 * 60 * 60) // TTL: 30 days
                .build();


        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(new OauthResponse(
                        response.getUser(),
                        response.getAccessToken(),
                        null
                ));
    }


    @Operation(summary = "Setup user", description = "Set up the user with the provided setup request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully set up user"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/setup")
    public UserDTO setup(@RequestBody @NotNull SetupRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return authService.setup(authId, request);
    }

    @Operation(summary = "Refresh token", description = "Refreshes the access token using the provided refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully refreshed token"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody @NotNull RefreshTokenRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        RefreshTokenResponse tokens = authService.refresh(authId, request);
        return ResponseEntity.ok()
                .header("Set-Cookie", tokens.getRefreshToken())
                .body(new RefreshTokenResponse(
                        tokens.getAccessToken(),
                        null
                ));
    }
}
