package viet.io.threadsbe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import viet.io.threadsbe.entity.User;

@Data
@AllArgsConstructor
public class UserListData {
    private User user;
    private long followerCount;
    private boolean isFollowing;
}