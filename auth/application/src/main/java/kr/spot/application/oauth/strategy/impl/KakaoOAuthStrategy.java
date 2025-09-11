package kr.spot.application.oauth.strategy.impl;


import kr.spot.application.oauth.strategy.OAuthStrategy;
import kr.spot.auth.api.dto.OAuthProfile;
import kr.spot.auth.domain.enums.LoginType;
import kr.spot.infrastructure.oauth.KaKaoOauth;
import kr.spot.infrastructure.oauth.client.dto.oauth.kakao.KaKaoOAuthTokenDTO;
import kr.spot.infrastructure.oauth.client.dto.oauth.kakao.KaKaoUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthStrategy implements OAuthStrategy {

    private final KaKaoOauth kaKaoOauth;

    @Override
    public LoginType getType() {
        return LoginType.KAKAO;
    }

    @Override
    public String getOauthRedirectURL() {
        return kaKaoOauth.getOauthRedirectURL();
    }

    @Override
    public OAuthProfile getOAuthProfile(String code) {
        KaKaoOAuthTokenDTO token = kaKaoOauth.requestAccessToken(code);
        KaKaoUser user = kaKaoOauth.requestUserInfo(token);
        return OAuthProfile.of(getType(), user.kakao_account().email(), user.properties().nickname(),
                user.properties().profile_image());
    }
}
