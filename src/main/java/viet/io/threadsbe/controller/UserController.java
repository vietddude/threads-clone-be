package viet.io.threadsbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import viet.io.threadsbe.dto.ProfileDTO;
import viet.io.threadsbe.dto.response.PaginatedResponse;
import viet.io.threadsbe.security.SecurityUtils;
import viet.io.threadsbe.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user profile", description = "Retrieve the profile of a user by their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved profile"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}")
    public ProfileDTO getProfile(@PathVariable String username) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return userService.getProfile(username, authId);
    }

    @Operation(summary = "Toggle follow user", description = "Toggle follow status for a user by their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully toggled follow status"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> toggleFollow(@PathVariable String username) {
        UUID authId = SecurityUtils.getCurrentUserId();
        userService.toggleFollowUser(username, authId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search users", description = "Retrieve a paginated list of users based on the provided query.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public PaginatedResponse<ProfileDTO> searchUser(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return userService.searchUser(query, page, limit, authId);
    }
}
