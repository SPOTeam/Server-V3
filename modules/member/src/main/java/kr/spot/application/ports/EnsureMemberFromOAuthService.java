package kr.spot.application.ports;

import kr.spot.Snowflake;
import kr.spot.base.enums.LoginType;
import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.Member;
import kr.spot.domain.vo.Email;
import kr.spot.exception.GeneralException;
import kr.spot.infrastructure.jpa.MemberRepository;
import kr.spot.ports.EnsureMemberFromOAuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnsureMemberFromOAuthService implements EnsureMemberFromOAuthPort {

    private final Snowflake snowflake = new Snowflake();
    private final MemberRepository memberRepository;

    @Override
    public long ensure(String provider, String email, String nickname, String imageUrl) {
        LoginType loginType = LoginType.valueOf(provider);
        validateIsUniqueEmailAndLoginType(email, loginType);
        Member save = createAndSaveMember(email, nickname, imageUrl, loginType);
        return save.getId();
    }

    private Member createAndSaveMember(String email, String nickname, String imageUrl, LoginType loginType) {
        Member member = Member.of(snowflake.nextId(), Email.of(email), nickname, loginType, imageUrl);
        return memberRepository.save(member);
    }

    private void validateIsUniqueEmailAndLoginType(String email, LoginType loginType) {
        if (memberRepository.existsByEmailAndLoginType(Email.of(email), loginType)) {
            throw new GeneralException(ErrorStatus._MEMBER_EMAIL_EXIST);
        }
    }
}
