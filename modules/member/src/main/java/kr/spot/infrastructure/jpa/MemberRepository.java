package kr.spot.infrastructure.jpa;

import java.util.Optional;
import kr.spot.domain.Member;
import kr.spot.domain.enums.LoginType;
import kr.spot.domain.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailAndLoginType(Email email, LoginType loginType);

    Optional<Member> findByEmailAndLoginType(Email email, LoginType loginType);
}
