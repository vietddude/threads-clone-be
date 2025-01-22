package viet.io.threadsbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Post", description = "Post API")
public class PostController {
    private final PostService postService;

    @Operation(summary = "Create post", description = "Create a new post with the provided request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created post"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public PostResponse createPost(@RequestBody CreatePostRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.createPost(request, authId);
    }

    @Operation(summary = "Reply to post", description = "Reply to an existing post with the provided request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully replied to post"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/replies")
    public PostResponse replyPost(@Valid @RequestBody ReplyPostRequest request) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.replyPost(request, authId);
    }

    @Operation(summary = "Repost to post", description = "Repost an existing post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully reposted"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/repost")
    public ToggleResponse toggleRepost(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.toggleRepost(id, authId);
    }

    @Operation(summary = "Toggle like post", description = "Toggle like status for a post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully toggled like status"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        postService.toggleLike(id, authId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Query posts", description = "Retrieve a paginated list of posts based on the provided query and author.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved posts"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public PaginatedResponse<PostDTO> queryPosts(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String author) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.queryPosts(page, limit, query, author, authId);
    }


    @Operation(summary = "Get post", description = "Retrieve a post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved post"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getPost(id, authId);
    }

    @Operation(summary = "Get nested posts", description = "Retrieve nested posts for a given post ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved nested posts"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/nested")
    public NestedPostResponse getNestedPosts(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getNestedPosts(id, authId);
    }

    @Operation(summary = "Get like info", description = "Retrieve like information for a given post ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved like info"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/like-info")
    public LikeInfoResponse getLikeInfo(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getLikeInfo(id, authId);
    }

    @Operation(summary = "Delete post", description = "Delete a post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted post"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        UUID authId = SecurityUtils.getCurrentUserId();
        postService.deletePost(id, authId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user replies", description = "Retrieve a paginated list of replies for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved replies"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/replies")
    public PaginatedResponse<PostDTO> getUserReplies(
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getRepliesByUsername(authId, author, page, limit);
    }

    @Operation(summary = "Get user reposts", description = "Retrieve a paginated list of reposts for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved reposts"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/reposts")
    public PaginatedResponse<PostDTO> getUserReposts(
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        UUID authId = SecurityUtils.getCurrentUserId();
        return postService.getRepostsByUsername(authId, author, page, limit);
    }

}
