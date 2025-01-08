package viet.io.threadsbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import viet.io.threadsbe.dto.projection.PostLikeCount;
import viet.io.threadsbe.dto.projection.PostReplyCount;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostMetadataDTO {
    private List<PostLikeCount> likeCounts;
    private List<PostReplyCount> replyCounts;
    private Map<UUID, List<CompactUserDTO>> replies;
    private Set<UUID> likedPostIds;
    private Set<UUID> repostedPostIds;
}
