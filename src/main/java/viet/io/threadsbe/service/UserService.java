package viet.io.threadsbe.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import viet.io.threadsbe.dto.CompactUserDTO;
import viet.io.threadsbe.dto.ProfileDTO;
import viet.io.threadsbe.dto.UserDTO;
import viet.io.threadsbe.dto.projection.UserListData;
import viet.io.threadsbe.dto.response.PaginatedResponse;
import viet.io.threadsbe.entity.Follow;
import viet.io.threadsbe.entity.User;
import viet.io.threadsbe.mapper.UserMapper;
import viet.io.threadsbe.repository.FollowRepository;
import viet.io.threadsbe.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public ProfileDTO getProfile(String username, UUID authId) {
        log.debug("Getting profile of user: {}", username);

        // Lấy thông tin User
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Lấy số lượng follower
        long followerCount = userRepository.countFollowers(user);

        // Kiểm tra xem người dùng có đang follow hay không
        boolean isFollowing = userRepository.isFollowing(authId, user);

        // Lấy danh sách follower gần nhất
        List<User> followers = userRepository.findRecentFollowers(user);

        // Chuyển đổi thành DTO
        UserDTO userDTO = UserMapper.INSTANCE.userToUserDTO(user);
        userDTO.setFollowing(isFollowing);
        List<CompactUserDTO> followerDTOs = followers.stream()
                .map(UserMapper.INSTANCE::userToCompactUserDTO)
                .collect(Collectors.toList());

        // Trả về ProfileDTO
        return new ProfileDTO(userDTO, user.getCreatedAt(), followerCount, followerDTOs);
    }


    public PaginatedResponse<ProfileDTO> searchUser(String query, int page, int limit, UUID authId) {
        int queryPage = Math.max(page - 1, 0);
        log.debug("Searching users with query: {}", query);


        Pageable pageable = PageRequest.of(queryPage, limit);
        Page<UserListData> usersData = query == null || query.isBlank()
                ? userRepository.getUsersWithFollowData(authId, pageable)
                : userRepository.findAllByUsernameContaining(query, pageable)
                .map(user -> new UserListData(user,
                        followRepository.countByFollowee(user),
                        followRepository.existsByFollowerIdAndFolloweeId(authId, user.getId())));

        List<ProfileDTO> profiles = usersData.getContent().stream()
                .map(data -> {
                    UserDTO userDTO = UserMapper.INSTANCE.userToUserDTO(data.getUser());
                    return new ProfileDTO(userDTO, data.getUser().getCreatedAt(), data.getFollowerCount(), null);
                })
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                profiles,
                page,
                limit,
                (int) usersData.getTotalElements()
        );
    }

    @Transactional
    public void toggleFollowUser(String targetUsername, UUID authId) {
        log.debug("Toggling follow for user: {}", targetUsername);

        // Get both users in a single query
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + targetUsername));
        User currentUser = userRepository.findById(authId)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found: " + authId));

        // Check follow status in a single query
        boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUser.getId(), targetUser.getId());

        if (isFollowing) {
            // Unfollow user
            followRepository.deleteByFollowerIdAndFolloweeId(currentUser.getId(), targetUser.getId());
            log.info("Unfollowed user: {}", targetUsername);
            notificationService.deleteBySenderIdAndReceiverId(currentUser.getId(), targetUser.getId());
        } else {
            // Follow user
            Follow follow = Follow.builder()
                    .follower(currentUser)
                    .followee(targetUser)
                    .build();
            followRepository.save(follow);
            log.info("Followed user: {}", targetUsername);

            notificationService.createFollowNotification(currentUser, targetUser);
        }
    }
}
