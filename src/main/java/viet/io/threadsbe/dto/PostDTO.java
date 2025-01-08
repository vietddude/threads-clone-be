package viet.io.threadsbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PostDTO {
    private UUID id;
    private String text;
    private LocalDateTime createdAt;
    private UserDTO author;
    private CountDTO count;
    private UUID parentPostId;
    private String[] images;
    private UUID quoteId;
    private List<CompactUserDTO> replies;
    private boolean liked;
    private boolean reposted;

    @Data
    @AllArgsConstructor
    public static class CountDTO {
        private long likeCount;
        private long replyCount;
    }

}
