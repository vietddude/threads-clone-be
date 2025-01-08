package viet.io.threadsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import viet.io.threadsbe.dto.UserDTO;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private UUID postId;
    private UserDTO author;
}