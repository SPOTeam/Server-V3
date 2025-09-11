package kr.spot.application.oauth;


import kr.spot.application.oauth.strategy.OAuthStrategy;
import kr.spot.auth.api.OAuthProfilePort;
import kr.spot.auth.api.dto.OAuthProfile;
import kr.spot.auth.api.dto.TokenDTO;
import kr.spot.auth.domain.enums.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService implements OAuthProfilePort {

    private final OAuthStrategyFactory strategyFactory;

    public String redirectURL(LoginType type) {
        return strategyFactory.getStrategy(type).getOauthRedirectURL();
    }

    public TokenDTO loginOrSignUp(LoginType type, String code) {
        OAuthStrategy strategy = strategyFactory.getStrategy(type);
        return null;
    }

    @Override
    public OAuthProfile getOAuthProfile(LoginType loginType, String code) {
        OAuthStrategy strategy = strategyFactory.getStrategy(loginType);
        return strategy.getOAuthProfile(code);
    }
}
