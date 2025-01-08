package viet.io.threadsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import viet.io.threadsbe.dto.UserDTO;

import java.util.List;

@Data
@AllArgsConstructor
public class LikeInfoResponse {
    private long likeCount;
    private long repostCount;
    private List<UserDTO> likes;
}
