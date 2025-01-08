package viet.io.threadsbe.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class FollowRequest {
    private UUID userId;
}
