package viet.io.threadsbe.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import viet.io.threadsbe.utils.enums.Privacy;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SearchUserResponse {
    private UUID id;
    private String image;
    private String fullname;
    private String username;
    private String bio;
    private String link;
    private Privacy privacy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

    @JsonProperty("isVerified")
    private boolean isVerified;

    @Setter
    @JsonProperty("isFollowing")
    private boolean isFollowing;

}


