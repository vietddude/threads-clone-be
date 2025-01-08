package viet.io.threadsbe.dto.projection;

import java.util.UUID;

public interface PostLikeCount {
    UUID getPostId();
    Long getLikeCount();
}
