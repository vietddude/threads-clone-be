package viet.io.threadsbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follows",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"follower_id", "followee_id"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "follower_id", referencedColumnName = "id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followee_id", referencedColumnName = "id", nullable = false)
    private User followee;

}
