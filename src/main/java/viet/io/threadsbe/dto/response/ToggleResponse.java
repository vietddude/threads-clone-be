package viet.io.threadsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ToggleResponse {
    private UUID postId;
    private boolean status;
}

