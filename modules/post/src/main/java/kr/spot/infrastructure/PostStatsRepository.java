package kr.spot.infrastructure;

import kr.spot.domain.PostStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStatsRepository extends JpaRepository<PostStats, Long> {

    @Modifying
    @Query("update PostStats s set s.likeCount = s.likeCount + 1 where s.postId = :postId")
    int increaseLike(@Param("postId") long postId);

    @Modifying
    @Query("update PostStats s set s.likeCount = case when s.likeCount > 0 then s.likeCount - 1 else 0 end where s.postId = :postId")
    int decreaseLike(@Param("postId") long postId);

}
