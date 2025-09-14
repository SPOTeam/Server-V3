package kr.spot.infrastructure.jpa;

import kr.spot.base.enums.LoginType;
import kr.spot.domain.Member;
import kr.spot.domain.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailAndLoginType(Email email, LoginType loginType);
}
