package viet.io.threadsbe.dto.projection;

import java.util.UUID;

public interface NestedProjection {
    UUID getId();

    UUID parentPostId();

    int getDepth();
}
