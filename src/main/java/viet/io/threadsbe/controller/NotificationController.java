package viet.io.threadsbe.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import viet.io.threadsbe.dto.NotificationDTO;
import viet.io.threadsbe.dto.response.PaginatedResponse;
import viet.io.threadsbe.security.SecurityUtils;
import viet.io.threadsbe.service.NotificationService;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
@Tag(name = "Notification", description = "Notification API")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Get notifications", description = "Retrieve a paginated list of notifications for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public PaginatedResponse<NotificationDTO> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return notificationService.getNotifications(page, limit, authId);
    }
}
