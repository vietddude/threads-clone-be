package viet.io.threadsbe.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInfo {
    @JsonProperty("email")
    private String email;

    @JsonProperty("verified_email")
    private boolean emailVerified;

    @JsonProperty("name")
    private String name;

    @JsonProperty("picture")
    private String picture;
}