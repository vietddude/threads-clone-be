package viet.io.threadsbe.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import viet.io.threadsbe.dto.NotificationDTO;
import viet.io.threadsbe.dto.PostDTO;
import viet.io.threadsbe.dto.response.PaginatedResponse;
import viet.io.threadsbe.entity.Notification;
import viet.io.threadsbe.entity.Post;
import viet.io.threadsbe.entity.User;
import viet.io.threadsbe.mapper.UserMapper;
import viet.io.threadsbe.repository.NotificationRepository;
import viet.io.threadsbe.utils.enums.NotificationType;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public PaginatedResponse<NotificationDTO> getNotifications(int page, int limit, UUID authId) {
        int queryPage = Math.max(page - 1, 0);
        log.info("Getting notifications for user: {}", authId);
        Pageable pageable = PageRequest.of(queryPage, limit, Sort.by("createdAt").descending());

        Page<Notification> notifications = notificationRepository.findNotificationsForUser(authId, pageable);

        List<NotificationDTO> notificationDTOs = notifications.getContent().stream()
                .map(notification -> NotificationDTO.builder()
                        .id(notification.getId())
                        .type(notification.getType())
                        .createdAt(notification.getCreatedAt())
                        .read(notification.isRead())
                        .message(notification.getMessage())
                        .post(notification.getPost() != null ? PostDTO.builder()
                                .id(notification.getPost().getId())
                                .text(notification.getPost().getText())
                                .build() : null)
                        .senderUser(notification.getSender() != null ? UserMapper.INSTANCE.userToUserDTO(notification.getSender()) : null)
                        .build())
                .toList();

        return new PaginatedResponse<>(notificationDTOs, page, limit, (int) notifications.getTotalElements());
    }

    public void createFollowNotification(User sender, User receiver) {
        Notification notification = Notification.builder()
                .type(NotificationType.FOLLOW)
                .sender(sender)
                .receiver(receiver)
                .message(sender.getUsername() + " followed you")
                .build();
        notificationRepository.save(notification);
    }

    public void createQuoteNotification(User sender, User receiver, Post post) {
        Notification notification = Notification.builder()
                .type(NotificationType.QUOTE)
                .sender(sender)
                .receiver(receiver)
                .post(post)
                .message(post.getText())
                .build();
        notificationRepository.save(notification);
    }

    public void createLikeNotification(User sender, User receiver, Post post) {
        Notification notification = Notification.builder()
                .type(NotificationType.LIKE)
                .sender(sender)
                .receiver(receiver)
                .post(post)
                .message(sender.getUsername() + " liked your post")
                .build();
        notificationRepository.save(notification);
    }

    public void createRepostNotification(User sender, User receiver, Post post) {
        Notification notification = Notification.builder()
                .type(NotificationType.REPOST)
                .sender(sender)
                .receiver(receiver)
                .post(post)
                .message(post.getText())
                .build();
        notificationRepository.save(notification);
    }

    public void createReplyNotification(User sender, User receiver, Post post) {
        Notification notification = Notification.builder()
                .type(NotificationType.REPLY)
                .sender(sender)
                .receiver(receiver)
                .post(post)
                .message(post.getText())
                .build();
        notificationRepository.save(notification);
    }

    public void deleteBySenderIdAndReceiverId(UUID sender, UUID receiver) {
        notificationRepository.deleteBySenderIdAndReceiverId(sender, receiver);
    }
}
