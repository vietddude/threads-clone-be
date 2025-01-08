package viet.io.threadsbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import viet.io.threadsbe.entity.Repost;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RepostRepository extends JpaRepository<Repost, UUID> {

    @Query("SELECT r.post.id FROM Repost r WHERE r.user.id = :userId AND r.post.id IN :postIds")
    Set<UUID> findRepostedPostIdsByUser(@Param("userId") UUID userId, @Param("postIds") List<UUID> postIds);

    Long countByPostId(UUID postId);

    Page<Repost> findByUserId(UUID userId, Pageable pageable);

    Optional<Repost> findByPostIdAndUserId(UUID postId, UUID userId);
}
