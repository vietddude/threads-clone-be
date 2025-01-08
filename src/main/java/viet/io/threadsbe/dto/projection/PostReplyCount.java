package viet.io.threadsbe.dto.projection;

import java.util.UUID;

public interface PostReplyCount {
    UUID getPostId();

    Long getReplyCount();
}
