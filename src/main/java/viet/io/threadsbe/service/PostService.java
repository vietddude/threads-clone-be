package viet.io.threadsbe.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import viet.io.threadsbe.dto.*;
import viet.io.threadsbe.dto.projection.NestedProjection;
import viet.io.threadsbe.dto.projection.PostLikeCount;
import viet.io.threadsbe.dto.projection.PostReplyCount;
import viet.io.threadsbe.dto.projection.RepliesResult;
import viet.io.threadsbe.dto.request.CreatePostRequest;
import viet.io.threadsbe.dto.request.ReplyPostRequest;
import viet.io.threadsbe.dto.response.*;
import viet.io.threadsbe.entity.Like;
import viet.io.threadsbe.entity.Post;
import viet.io.threadsbe.entity.Repost;
import viet.io.threadsbe.entity.User;
import viet.io.threadsbe.mapper.PostMapper;
import viet.io.threadsbe.mapper.UserMapper;
import viet.io.threadsbe.repository.*;
import viet.io.threadsbe.utils.enums.NotificationType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final RepostRepository repostRepository;
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    private String[] processImages(String image) {
        return (image != null && !image.isEmpty()) ? image.split(",") : new String[0];
    }

    private PostMetadataDTO fetchPostMetadata(List<UUID> postIds, UUID userId) {
        // Sử dụng CompletableFuture để chạy song song các tác vụ bất đồng bộ
        CompletableFuture<List<PostLikeCount>> likeCountsFuture = CompletableFuture.supplyAsync(() -> likeRepository.countLikesByPostIds(postIds));
        CompletableFuture<List<PostReplyCount>> replyCountsFuture = CompletableFuture.supplyAsync(() -> postRepository.countRepliesByParentPostIds(postIds));
        CompletableFuture<Map<UUID, List<CompactUserDTO>>> repliesFuture = CompletableFuture.supplyAsync(() -> postRepository.findRepliesByParentPostIds(postIds).stream()
                .collect(Collectors.groupingBy(
                        RepliesResult::getPostId,
                        Collectors.mapping(
                                reply -> new CompactUserDTO(reply.getUser().getId(), reply.getUser().getUsername(), reply.getUser().getImage()),
                                Collectors.toList()
                        )
                )));
        CompletableFuture<Set<UUID>> likedPostIdsFuture = CompletableFuture.supplyAsync(() -> userId != null ? likeRepository.findLikedPostIdsByUser(userId, postIds) : Set.of());
        CompletableFuture<Set<UUID>> repostedPostIdsFuture = CompletableFuture.supplyAsync(() -> userId != null ? repostRepository.findRepostedPostIdsByUser(userId, postIds) : Set.of());

        // Chờ đợi tất cả các tác vụ bất đồng bộ hoàn thành
        CompletableFuture.allOf(likeCountsFuture, replyCountsFuture, repliesFuture, likedPostIdsFuture, repostedPostIdsFuture).join();

        // Lấy kết quả từ các CompletableFuture
        List<PostLikeCount> likeCounts = likeCountsFuture.join();
        List<PostReplyCount> repliesCount = replyCountsFuture.join();
        Map<UUID, List<CompactUserDTO>> replies = repliesFuture.join();
        Set<UUID> likedPostIds = likedPostIdsFuture.join();
        Set<UUID> repostedPostIds = repostedPostIdsFuture.join();

        // Trả về kết quả cuối cùng
        return new PostMetadataDTO(likeCounts, repliesCount, replies, likedPostIds, repostedPostIds);
    }

    private PostDTO mapPostToDTO(Post post, PostMetadataDTO metadata) {
        // Lấy giá trị likeCount từ metadata cho post, nếu không có thì trả về 0
        Long likeCount = metadata.getLikeCounts().stream()
                .filter(count -> count.getPostId().equals(post.getId()))
                .map(PostLikeCount::getLikeCount)
                .findFirst()
                .orElse(0L);

        Long replyCount = metadata.getReplyCounts().stream()
                .filter(count -> count.getPostId().equals(post.getId()))
                .map(PostReplyCount::getReplyCount)
                .findFirst()
                .orElse(0L);


        // Lấy replies từ metadata cho post, nếu không có thì trả về một list rỗng
        List<CompactUserDTO> replies = metadata.getReplies().getOrDefault(post.getId(), List.of());

        // Kiểm tra xem post có được like và reposted hay không
        boolean liked = metadata.getLikedPostIds().contains(post.getId());
        boolean reposted = metadata.getRepostedPostIds().contains(post.getId());

        return PostDTO.builder()
                .id(post.getId())
                .text(post.getText())
                .createdAt(post.getCreatedAt())
                .author(UserMapper.INSTANCE.userToUserDTO(post.getAuthor()))
                .count(new PostDTO.CountDTO(likeCount, replyCount))  // Sử dụng likeCount và số lượng reply
                .parentPostId(Optional.ofNullable(post.getParentPost()).map(Post::getId).orElse(null))
                .images(processImages(post.getImage()))  // Giả sử processImages là phương thức xử lý ảnh
                .quoteId(Optional.ofNullable(post.getQuotePost()).map(Post::getId).orElse(null))
                .replies(replies)
                .liked(liked)
                .reposted(reposted)
                .build();
    }

    public PostResponse createPost(CreatePostRequest request, UUID authId) {
        log.info("Creating post for user with ID: {}", authId);
        User user = userRepository.findById(authId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post quotePost = null;
        User postAuthor = null;

        if (request.getQuoteId() != null) {
            quotePost = postRepository.findById(request.getQuoteId())
                    .orElseThrow(() -> new EntityNotFoundException("Quote post not found"));

            postAuthor = userRepository.findById(request.getPostAuthor())
                    .orElseThrow(() -> new EntityNotFoundException("Post author not found"));

            if (!quotePost.getAuthor().equals(postAuthor)) {
                throw new IllegalArgumentException("Post author does not match the quote post author");
            }
        }

        Post post = Post.builder()
                .author(user)
                .text(request.getText())
                .quotePost(quotePost)
                .privacy(request.getPrivacy())
                .image(request.getImages() != null ? String.join(",", request.getImages()) : null)
                .build();

        // Send notification if the post is a quote
        if (postAuthor != null) {
            notificationService.createQuoteNotification(user, postAuthor, post);
        }

        Post savedPost = postRepository.save(post);

        return new PostResponse(savedPost.getId(), UserMapper.INSTANCE.userToUserDTO(user));
    }

    public void deletePost(UUID id, UUID authId) {
        Post post = postRepository.findById(id)
                .filter(p -> p.getAuthor().getId().equals(authId))
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        List<Post> linkedPosts = postRepository.findPostsByParentPostOrQuotePost(post);

        // Xóa tất cả bài viết liên quan
        Stream.concat(Stream.of(post), linkedPosts.stream())
                .forEach(p -> {
                    likeRepository.deleteByPostId(p.getId());
                    postRepository.delete(p);
                });
    }

    public void toggleLike(UUID postId, UUID authId) {
        User user = userRepository.findById(authId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, user.getId());

        if (existingLike.isPresent()) {
            // Nếu đã like, xóa like
            likeRepository.delete(existingLike.get());
            notificationRepository.findBySenderAndPostAndType(user, post, NotificationType.LIKE)
                    .ifPresent(notificationRepository::delete);
        } else {
            // Nếu chưa like, tạo like mới
            likeRepository.save(new Like(post, user));
            notificationService.createLikeNotification(user, post.getAuthor(), post);
        }
    }

    public ToggleResponse toggleRepost(UUID postId, UUID authId) {
        User user = userRepository.findById(authId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Optional<Repost> existingRepost = repostRepository.findByPostIdAndUserId(postId, user.getId());

        if (existingRepost.isPresent()) {
            // Nếu đã repost, xóa repost
            repostRepository.delete(existingRepost.get());
            notificationRepository.findBySenderAndPostAndType(user, post, NotificationType.REPOST)
                    .ifPresent(notificationRepository::delete);
            return new ToggleResponse(postId, false);
        } else {
            // Nếu chưa repost, tạo repost mới
            repostRepository.save(new Repost(post, user));
            notificationService.createRepostNotification(user, post.getAuthor(), post);
            return new ToggleResponse(postId, true);
        }
    }

    public PostResponse replyPost(ReplyPostRequest request, UUID authId) {
        User user = userRepository.findById(authId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post parentPost = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Parent post not found"));

        Post replyPost = Post.builder()
                .author(user)
                .text(request.getText())
                .image(String.join(",", request.getImages()))
                .parentPost(parentPost)
                .quotePost(null)
                .build();

        postRepository.save(replyPost);
        notificationService.createReplyNotification(user, parentPost.getAuthor(), replyPost);

        return new PostResponse(replyPost.getId(), UserMapper.INSTANCE.userToUserDTO(user));
    }

    @Transactional
    public LikeInfoResponse getLikeInfo(UUID postId, UUID authId) {

        long likeCount = likeRepository.countByPostId(postId);
        long repostCount = repostRepository.countByPostId(postId);

        if (likeCount == 0) return new LikeInfoResponse(likeCount, repostCount, List.of());

        Pageable pageable = PageRequest.of(0, 10);
        List<Like> likes = likeRepository.findUsersByPostId(postId, pageable);

        // Truy vấn danh sách follow một lần
        List<UUID> likeUserIds = likes.stream()
                .map(like -> like.getUser().getId())
                .toList();

        List<UserDTO> likers = likes.stream()
                .map(like -> {
                    User likeUser = like.getUser();
                    return UserMapper.INSTANCE.userToUserDTO(likeUser);
                })
                .toList();

        return new LikeInfoResponse(likeCount, repostCount, likers);
    }

    public PostDTO getPost(UUID id, UUID authId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        PostMetadataDTO metadata = fetchPostMetadata(List.of(id), authId);
        return mapPostToDTO(post, metadata);
    }

    public PaginatedResponse<PostDTO> queryPosts(int page, int limit, String queryString, String author, UUID authId) {
        int queryPage = Math.max(page - 1, 0);
        log.info("Querying posts with author: {}, query: {}", author, queryString);

        Pageable pageable = PageRequest.of(queryPage, limit, Sort.by("createdAt").descending());

        // Tạo Specification để lọc các bài viết
        Specification<Post> spec = Specification.where(null);

        if (author != null && !author.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("author").get("username"), "%" + author + "%"));
        }

        if (queryString != null && !queryString.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("text"), "%" + queryString + "%"));
        }

        spec = spec.and((root, query, cb) -> cb.isNull(root.get("parentPost")));

        // Truy vấn các bài viết với các điều kiện và phân trang
        Page<Post> postsPage = postRepository.findAll(spec, pageable);

        if (postsPage.isEmpty()) {
            return new PaginatedResponse<>(List.of(), page, limit, 0);
        }

        // Lấy metadata cho từng bài viết (sử dụng Map để ánh xạ nhanh)
        List<UUID> postIds = postsPage.getContent().stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        // Fetch metadata một lần và lưu vào Map
        PostMetadataDTO postMetadataList = fetchPostMetadata(postIds, authId);

        // Ánh xạ bài viết và metadata vào PostDTO
        List<PostDTO> posts = postsPage.getContent().stream()
                .map(post -> mapPostToDTO(post, postMetadataList))
                .collect(Collectors.toList());

        // Trả về kết quả phân trang
        return new PaginatedResponse<>(posts, page, limit, (int) postsPage.getTotalElements());
    }

    public PaginatedResponse<PostDTO> getRepliesByUsername(UUID authId, String username, int page, int limit) {
        int queryPage = Math.max(page - 1, 0);
        log.info("Fetching replies for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + username));

        Pageable pageable = PageRequest.of(queryPage, limit, Sort.by("createdAt").descending());
        Page<Post> repliesPage = postRepository.findAllByAuthorIdAndParentPostNotNull(user.getId(), pageable);

        if (repliesPage.isEmpty()) {
            return new PaginatedResponse<>(List.of(), 0, 0, 0);
        }

        List<UUID> replyIds = repliesPage.getContent().stream()
                .map(Post::getId)
                .toList();

        PostMetadataDTO metadata = fetchPostMetadata(replyIds, authId);

        List<PostDTO> replyDTOs = repliesPage.getContent().stream()
                .map(reply -> mapPostToDTO(reply, metadata))
                .toList();

        return new PaginatedResponse<>(
                replyDTOs,
                page,
                limit,
                (int) repliesPage.getTotalElements()
        );
    }

    public PaginatedResponse<PostDTO> getRepostsByUsername(UUID authId, String username, int page, int limit) {
        int queryPage = Math.max(page - 1, 0);
        log.info("Fetching repostsPage for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + username));

        Pageable pageable = PageRequest.of(queryPage, limit, Sort.by("createdAt").descending());
        Page<Repost> repostsPage = repostRepository.findByUserId(user.getId(), pageable);

        if (repostsPage.isEmpty()) {
            return new PaginatedResponse<>(List.of(), 0, 0, 0);
        }

        List<UUID> repostIds = repostsPage.stream()
                .map(repost -> repost.getPost().getId())
                .toList();

        List<Post> posts = postRepository.findAllById(repostIds);
        PostMetadataDTO metadata = fetchPostMetadata(repostIds, authId);

        List<PostDTO> repostDTOs = posts.stream()
                .map(post -> mapPostToDTO(post, metadata))
                .toList();


        return new PaginatedResponse<>(
                repostDTOs,
                page,
                limit,
                (int) repostsPage.getTotalElements()
        );
    }

    public NestedPostResponse getNestedPosts(UUID postId, UUID authId) {
        // Fetch main post and replies
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        List<Post> replies = postRepository.findByParentPostId(postId);

        // Combine posts and fetch metadata
        List<Post> combinedPosts = Stream.concat(Stream.of(post), replies.stream()).toList();
        List<UUID> combinedIds = combinedPosts.stream().map(Post::getId).toList();
        PostMetadataDTO metadataDTO = fetchPostMetadata(combinedIds, authId);

        // Map to DTOs
        Map<UUID, PostDTO> postDTOMap = combinedPosts.stream()
                .map(reply -> mapPostToDTO(reply, metadataDTO))
                .collect(Collectors.toMap(PostDTO::getId, dto -> dto));

        // Create main post DTO and assign replies
        PostDTO postDTO = postDTOMap.get(postId);
        NestedPostDTO postInfo = PostMapper.INSTANCE.toNestedPostDTO(postDTO);
        postInfo.setReplies(replies.stream().map(reply -> postDTOMap.get(reply.getId())).toList());

        // Fetch nested posts
        List<NestedProjection> nestedPosts = postRepository.findNestedPosts(postId);
        List<UUID> nestedPostIds = nestedPosts.stream().map(NestedProjection::getId).toList();
        List<Post> nestedPostsList = postRepository.findAllById(nestedPostIds);

        // Map nested posts to DTOs
        PostMetadataDTO nestedMetadata = fetchPostMetadata(nestedPostIds, authId);
        List<PostDTO> parentPostDTOs = nestedPostsList.stream()
                .filter(nestedPost -> !nestedPost.getId().equals(postId))
                .map(nestedPost -> mapPostToDTO(nestedPost, nestedMetadata))
                .toList();

        return new NestedPostResponse(postInfo, parentPostDTOs);
    }
}