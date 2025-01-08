package viet.io.threadsbe.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import viet.io.threadsbe.dto.PostDTO;
import viet.io.threadsbe.dto.request.CreatePostRequest;
import viet.io.threadsbe.dto.request.ReplyPostRequest;
import viet.io.threadsbe.dto.response.*;
import viet.io.threadsbe.security.SecurityUtils;
import viet.io.threadsbe.service.PostService;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    // Create bài viết
    @PostMapping
    public PostResponse createPost(@RequestBody CreatePostRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.createPost(request, authId);
    }

    // Reply to post
    @PostMapping("/replies")
    public PostResponse replyPost(@Valid @RequestBody ReplyPostRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.replyPost(request, authId);
    }

    // Repost to post
    @PostMapping("/{id}/repost")
    public ToggleResponse toggleRepost(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.toggleRepost(id, authId);
    }

    // Toggle like post
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        postService.toggleLike(id, authId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PaginatedResponse<PostDTO> queryPosts(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String author) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.queryPosts(page, limit, query, author, authId);
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getPost(id, authId);
    }

    @GetMapping("/{id}/nested")
    public NestedPostResponse getNestedPosts(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getNestedPosts(id, authId);
    }

    @GetMapping("/{id}/like-info")
    public LikeInfoResponse getLikeInfo(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getLikeInfo(id, authId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        postService.deletePost(id, authId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/replies")
    public PaginatedResponse<PostDTO> getUserReplies(
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getRepliesByUsername(authId, author, page, limit);
    }

    @GetMapping("/reposts")
    public PaginatedResponse<PostDTO> getUserReposts(
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getRepostsByUsername(authId, author, page, limit);
    }

}
