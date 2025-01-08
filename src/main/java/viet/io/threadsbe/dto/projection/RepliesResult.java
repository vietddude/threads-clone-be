package viet.io.threadsbe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import viet.io.threadsbe.dto.CompactUserDTO;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RepliesResult {
    private UUID postId;
    private CompactUserDTO user;
}
