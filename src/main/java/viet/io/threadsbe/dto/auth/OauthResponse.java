package viet.io.threadsbe.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import viet.io.threadsbe.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
public class OauthResponse {
    private UserDTO user;
    private String accessToken;
    private String refreshToken;
}
