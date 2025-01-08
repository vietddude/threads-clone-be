package viet.io.threadsbe.entity;

import jakarta.persistence.*;
import lombok.*;
import viet.io.threadsbe.utils.enums.NotificationType;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private boolean read = false; // Default value is false

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false) // Ensure 'type' is not null
    private NotificationType type; // 'ADMIN', 'LIKE', 'REPLY', 'FOLLOW', 'REPOST', 'QUOTE'

    private String message;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false; // Default value is false

    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
