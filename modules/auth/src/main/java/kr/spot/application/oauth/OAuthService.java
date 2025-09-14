package kr.spot.application.oauth;


import kr.spot.application.oauth.strategy.OAuthStrategy;
import kr.spot.base.enums.LoginType;
import kr.spot.presentation.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthStrategyFactory strategyFactory;
    private final OAuthMemberProcessor oAuthMemberProcessor;

    public String redirectURL(LoginType type) {
        return strategyFactory.getStrategy(type).getOauthRedirectURL();
    }

    public TokenDTO getOAuthProfile(LoginType loginType, String code) {
        OAuthStrategy strategy = strategyFactory.getStrategy(loginType);
        return oAuthMemberProcessor.processOAuthMember(strategy.getOAuthProfile(code));
    }
}
