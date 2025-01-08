package viet.io.threadsbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import viet.io.threadsbe.dto.projection.PostReplyCount;
import viet.io.threadsbe.entity.Repost;

import java.util.*;

public interface RepostRepository  extends JpaRepository<Repost, UUID> {

    @Query("SELECT r.post.id as postId, COUNT(r) as count FROM Repost r WHERE r.post.id IN :postIds GROUP BY r.post.id")
    List<PostReplyCount> countRepostsByPostIds(@Param("postIds") List<UUID> postIds);

    @Query("SELECT r.post.id FROM Repost r WHERE r.user.id = :userId AND r.post.id IN :postIds")
    Set<UUID> findRepostedPostIdsByUser(@Param("userId") UUID userId, @Param("postIds") List<UUID> postIds);

    Long countByPostId(UUID postId);

    Page<Repost> findByUserId(UUID userId, Pageable pageable);

    Optional<Repost> findByPostIdAndUserId(UUID postId, UUID userId);
}
