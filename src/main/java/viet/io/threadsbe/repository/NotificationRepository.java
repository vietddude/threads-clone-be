package viet.io.threadsbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import viet.io.threadsbe.entity.Notification;
import viet.io.threadsbe.entity.Post;
import viet.io.threadsbe.entity.User;
import viet.io.threadsbe.utils.enums.NotificationType;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Query("SELECT n FROM Notification n " +
            "WHERE (n.isPublic = true AND n.type = 'ADMIN') " +
            "   OR (n.isPublic = false AND n.type = 'ADMIN' AND n.receiver.id = :userId) " +
            "   OR (n.isPublic = false AND n.receiver.id = :userId AND n.sender.id <> :userId)")
    Page<Notification> findNotificationsForUser(@Param("userId") UUID userId, Pageable pageable);

    void deleteBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

    void deleteByPost(Post post);

    Optional<Notification> findBySenderAndPostAndType(User sender, Post post, NotificationType type);
}
