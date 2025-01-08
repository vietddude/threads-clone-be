package viet.io.threadsbe.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import viet.io.threadsbe.utils.enums.Privacy;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Profile {
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
}

