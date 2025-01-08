package viet.io.threadsbe.controller;


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
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public PaginatedResponse<NotificationDTO> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return notificationService.getNotifications(page, limit, authId);
    }
}
