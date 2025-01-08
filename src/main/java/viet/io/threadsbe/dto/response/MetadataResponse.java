package viet.io.threadsbe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import viet.io.threadsbe.dto.CompactUserDTO;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MetadataResponse {
    private List<CompactUserDTO> recentFollowers;
    private long followersCount;
    @JsonProperty("isFollowing")
    private boolean isFollowing;
}
