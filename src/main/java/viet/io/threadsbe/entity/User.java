package viet.io.threadsbe.entity;

import jakarta.persistence.*;
import lombok.*;
import viet.io.threadsbe.utils.enums.Privacy;


@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    private String username;
    private String fullname;
    private String image;
    private String bio;
    private String link;
    private String email;
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy")
    private Privacy privacy = Privacy.PUBLIC; // 'PUBLIC' or 'PRIVATE'

    @Column(name = "is_admin")
    private boolean isAdmin = false;
}
