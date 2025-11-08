package kr.spot.infrastructure.jpa;

import kr.spot.domain.association.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    void deleteByPostId(Long postId);
}
