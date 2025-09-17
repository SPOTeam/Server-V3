package kr.spot.infrastructure.jpa;

import java.util.Optional;
import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.Member;
import kr.spot.domain.enums.LoginType;
import kr.spot.domain.vo.Email;
import kr.spot.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailAndLoginType(Email email, LoginType loginType);

    Optional<Member> findByEmailAndLoginType(Email email, LoginType loginType);

    default Member getMemberById(long id) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }
}
