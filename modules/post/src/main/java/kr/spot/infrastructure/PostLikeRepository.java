package kr.spot.infrastructure;

import kr.spot.domain.association.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    long deleteByPostIdAndMemberId(Long postId, Long memberId);
}
