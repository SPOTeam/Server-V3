package kr.spot.application.oauth;


import kr.spot.application.oauth.strategy.OAuthStrategy;
import kr.spot.base.enums.LoginType;
import kr.spot.presentation.dto.response.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthStrategyFactory strategyFactory;

    public String redirectURL(LoginType type) {
        return strategyFactory.getStrategy(type).getOauthRedirectURL();
    }

    public TokenResponseDTO loginOrSignUp(LoginType type, String code) {
        OAuthStrategy strategy = strategyFactory.getStrategy(type);
        return null;
    }
}
