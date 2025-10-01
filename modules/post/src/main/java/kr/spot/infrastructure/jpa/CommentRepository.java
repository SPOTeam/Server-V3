package kr.spot.infrastructure.jpa;

import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.Comment;
import kr.spot.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default Comment getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMENT_NOT_FOUND));
    }
}
