package viet.io.threadsbe.entity;

import jakarta.persistence.*;
import lombok.*;
import viet.io.threadsbe.utils.enums.PostPrivacy;


@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String text;

    private String image;

    @ManyToOne
    @JoinColumn(name = "parent_post_id")
    private Post parentPost;

    @ManyToOne
    @JoinColumn(name = "quote_id")
    private Post quotePost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostPrivacy privacy; // 'FOLLOWED', 'ANYONE', or 'MENTIONED'
}

