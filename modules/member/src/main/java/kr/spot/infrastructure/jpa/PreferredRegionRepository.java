package kr.spot.infrastructure.jpa;

import kr.spot.domain.association.PreferredRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredRegionRepository extends JpaRepository<PreferredRegion, Long> {

    void deleteAllByMemberId(Long memberId);
}
