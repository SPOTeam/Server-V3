package kr.spot.infrastructure.jpa;

import kr.spot.domain.association.PreferredCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredCategoryRepository extends JpaRepository<PreferredCategory, Long> {

    void deleteAllByMemberId(Long memberId);

}
