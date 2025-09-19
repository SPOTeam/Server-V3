package kr.spot.infrastructure;

import kr.spot.domain.association.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Modifying
    @Query(
            value = """
                    INSERT IGNORE INTO post_like(id, post_id, member_id, status, created_at, updated_at)
                    VALUES (:id, :postId, :memberId, 'ACTIVE', NOW(), NOW())
                    """, nativeQuery = true)
    int savePostLike(@Param("id") long id,
                     @Param("postId") long postId,
                     @Param("memberId") long memberId);

    long deleteByPostIdAndMemberId(Long postId, Long memberId);
}
