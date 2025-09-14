package kr.spot.application.oauth;

import kr.spot.Snowflake;
import kr.spot.application.oauth.dto.OAuthProfile;
import kr.spot.application.token.TokenProvider;
import kr.spot.domain.RefreshToken;
import kr.spot.infrastructure.jpa.RefreshTokenRepository;
import kr.spot.ports.EnsureMemberFromOAuthPort;
import kr.spot.presentation.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthMemberProcessor {

    private final Snowflake snowflake = new Snowflake();
    private final EnsureMemberFromOAuthPort ensureMemberFromOAuthPort;
    private final TokenProvider tokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;


    public TokenDTO processOAuthMember(OAuthProfile oAuthProfile) {
        long createdMemberId = ensureMemberFromOAuthPort.ensure(oAuthProfile.loginType().toString(),
                oAuthProfile.email(),
                oAuthProfile.nickname(),
                oAuthProfile.profileImageUrl());

        TokenDTO tokenDTO = tokenProvider.createToken(createdMemberId);
        saveRefreshToken(createdMemberId, tokenDTO);
        return tokenDTO;
    }

    private void saveRefreshToken(long createdMemberId, TokenDTO tokenDTO) {
        RefreshToken refreshToken = RefreshToken.of(snowflake.nextId(), createdMemberId, tokenDTO.refreshToken());
        refreshTokenRepository.save(refreshToken);
    }
}
