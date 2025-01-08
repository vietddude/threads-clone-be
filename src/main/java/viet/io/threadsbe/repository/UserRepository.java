package viet.io.threadsbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import viet.io.threadsbe.dto.projection.UserListData;
import viet.io.threadsbe.dto.projection.UserProfileData;
import viet.io.threadsbe.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Page<User> findAllByUsernameContaining(String username, Pageable pageable);

    // Query lấy số lượng follower
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee = :user")
    long countFollowers(@Param("user") User user);

    // Query kiểm tra xem người dùng có đang follow hay không
    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower.id = :authId AND f.followee = :user")
    boolean isFollowing(@Param("authId") UUID authId, @Param("user") User user);

    // Query lấy danh sách follower gần nhất
    @Query("SELECT f.follower FROM Follow f WHERE f.followee = :user ORDER BY f.createdAt DESC")
    List<User> findRecentFollowers(@Param("user") User user);

    @Query("""
        SELECT new viet.io.threadsbe.dto.projection.UserListData(
            u,
            COUNT(f1) as followerCount,
            EXISTS(SELECT 1 FROM Follow f2 WHERE f2.follower.id = :authId AND f2.followee = u) as isFollowing
        )
        FROM User u
        LEFT JOIN Follow f1 ON f1.followee = u
        GROUP BY u
        """)
    Page<UserListData> getUsersWithFollowData(@Param("authId") UUID authId, Pageable pageable);
}