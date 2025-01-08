package viet.io.threadsbe.dto;

import lombok.Builder;
import lombok.Data;
import viet.io.threadsbe.utils.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private NotificationType type;
    private String message;
    private boolean read;
    private PostDTO post;
    private UserDTO senderUser;
}
