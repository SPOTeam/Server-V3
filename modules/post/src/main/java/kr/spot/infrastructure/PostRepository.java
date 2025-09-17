package kr.spot.infrastructure;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.Post;
import kr.spot.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findByIdWithLock(long id);

    default Post getPostById(long id) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
    }

    default Post getPostByIdWithLock(long id) {
        return findByIdWithLock(id).orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
    }


}
