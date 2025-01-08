package viet.io.threadsbe.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import viet.io.threadsbe.dto.projection.PostLikeCount;
import viet.io.threadsbe.entity.Like;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    @Query("SELECT p.id AS postId, COUNT(l) AS likeCount " +
            "FROM Like l JOIN l.post p " +
            "WHERE p.id IN :postIds " +
            "GROUP BY p.id")
    List<PostLikeCount> countLikesByPostIds(@Param("postIds") List<UUID> postIds);

    @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId AND l.post.id IN :postIds")
    Set<UUID> findLikedPostIdsByUser(@Param("userId") UUID userId, @Param("postIds") List<UUID> postIds);

    Long countByPostId(UUID postId);

    void deleteByPostId(UUID postId);

    Optional<Like> findByPostIdAndUserId(UUID postId, UUID userId);

    List<Like> findUsersByPostId(UUID postId, Pageable pageable);

}