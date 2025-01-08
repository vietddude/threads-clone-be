package viet.io.threadsbe.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import viet.io.threadsbe.dto.ProfileDTO;
import viet.io.threadsbe.dto.response.*;
import viet.io.threadsbe.security.SecurityUtils;
import viet.io.threadsbe.service.UserService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    public ProfileDTO getProfile(@PathVariable String username) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return userService.getProfile(username, authId);
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> toggleFollow(@PathVariable String username) {
        UUID authId = SecurityUtils.getCurrentUserId();
        userService.toggleFollowUser(username, authId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PaginatedResponse<ProfileDTO> searchUser(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return userService.searchUser(query, page, limit, authId);
    }

}
