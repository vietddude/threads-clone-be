package viet.io.threadsbe.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viet.io.threadsbe.entity.Follow;
import viet.io.threadsbe.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);
    void deleteByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);
    List<Follow> findFollowsByFollowee(User followee, Pageable pageable);
    long countByFollowee(User followee);

    @Query("SELECT f FROM Follow f WHERE f.follower.id = :followerId AND f.followee.id IN :followeeIds")
    List<Follow> findByFollowerIdAndFolloweeIds(@Param("followerId") UUID followerId, @Param("followeeIds") List<UUID> followeeIds);
}