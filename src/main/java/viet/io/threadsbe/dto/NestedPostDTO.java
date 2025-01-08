package viet.io.threadsbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class NestedPostDTO {
    private UUID id;
    private String text;
    private LocalDateTime createdAt;
    private UserDTO author;
    private PostDTO.CountDTO count;
    private UUID parentPostId;
    private String[] images;
    private UUID quoteId;
    private List<PostDTO> replies;
    private boolean liked;
    private boolean reposted;
}
