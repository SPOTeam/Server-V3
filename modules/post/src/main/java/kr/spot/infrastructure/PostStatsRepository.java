package kr.spot.infrastructure;

import kr.spot.domain.PostStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostStatsRepository extends JpaRepository<PostStats, Long> {
}
