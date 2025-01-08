package viet.io.threadsbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import viet.io.threadsbe.dto.projection.NestedProjection;
import viet.io.threadsbe.dto.projection.PostReplyCount;
import viet.io.threadsbe.dto.projection.RepliesResult;
import viet.io.threadsbe.entity.Post;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = """
               WITH RECURSIVE PostTree AS (
                   SELECT
                       id,
                       parent_post_id,
                       0 as depth
                   FROM posts
                   WHERE id = :postId
                   
                   UNION ALL
                   
                   SELECT
                       p.id,
                       p.parent_post_id,
                       pt.depth + 1
                   FROM posts p
                   INNER JOIN PostTree pt ON p.id = pt.parent_post_id
               )
               SELECT id, parent_post_id as parentPostId, depth
               FROM PostTree
               ORDER BY depth DESC
            """, nativeQuery = true)
    List<NestedProjection> findNestedPosts(@Param("postId") UUID postId);

    @Query("""
            SELECT new viet.io.threadsbe.dto.projection.RepliesResult(
                p.parentPost.id,
                new viet.io.threadsbe.dto.CompactUserDTO(
                    p.author.id, p.author.username, p.author.image
                )
            )
            FROM Post p
            WHERE p.parentPost.id IN :parentPostIds
            ORDER BY p.createdAt DESC
            LIMIT 2
            """)
    List<RepliesResult> findRepliesByParentPostIds(@Param("parentPostIds") List<UUID> parentPostIds);

    List<Post> findByParentPostId(UUID parentPostId);

    @Query("SELECT p.parentPost.id as postId, COUNT(p) as replyCount FROM Post p WHERE p.parentPost.id IN :postIds GROUP BY p.parentPost.id")
    List<PostReplyCount> countRepliesByParentPostIds(@Param("postIds") List<UUID> postIds);

    @Query("SELECT p FROM Post p WHERE p.parentPost = :post OR p.quotePost = :post")
    List<Post> findPostsByParentPostOrQuotePost(Post post);

    Page<Post> findAll(Specification<Post> spec, Pageable pageable);

    Page<Post> findAllByAuthorIdAndParentPostNotNull(UUID authorId, Pageable pageable);
}

