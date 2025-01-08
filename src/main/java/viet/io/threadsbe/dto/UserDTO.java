package viet.io.threadsbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import viet.io.threadsbe.utils.enums.Privacy;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String username;
    private String fullname;
    private String image;
    private String bio;
    private String link;
    private String email;
    private boolean verified;
    private Privacy privacy;
    @JsonProperty("isAdmin")
    private boolean isAdmin;
    @JsonProperty("isFollowing")
    private boolean isFollowing;
}
