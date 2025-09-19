package kr.spot.infrastructure.jpa;

import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.PostStats;
import kr.spot.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStatsRepository extends JpaRepository<PostStats, Long> {

    default PostStats getPostStatsById(long postId) {
        return findById(postId).orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
    }

    @Modifying
    @Query("update PostStats s set s.likeCount = s.likeCount + 1 where s.postId = :postId")
    int increaseLike(@Param("postId") long postId);

    @Modifying
    @Query("update PostStats s set s.likeCount = case when s.likeCount > 0 then s.likeCount - 1 else 0 end where s.postId = :postId")
    int decreaseLike(@Param("postId") long postId);

}
