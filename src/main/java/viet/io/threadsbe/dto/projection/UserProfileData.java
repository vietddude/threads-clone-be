package viet.io.threadsbe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import viet.io.threadsbe.entity.User;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileData {
    private User user;  // Thông tin đầy đủ của user
    private long followerCount;  // Số lượng follower
    private boolean isFollowing;  // Kiểm tra nếu user đang follow
    private List<User> followers;  // Danh sách followers (dạng User)
}
