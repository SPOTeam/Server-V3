package kr.spot.application.oauth.strategy.impl;


import kr.spot.application.oauth.dto.OAuthProfile;
import kr.spot.application.oauth.strategy.OAuthStrategy;
import kr.spot.base.enums.LoginType;
import kr.spot.infrastructure.oauth.NaverOauth;
import kr.spot.infrastructure.oauth.client.dto.oauth.naver.NaverOAuthTokenDTO;
import kr.spot.infrastructure.oauth.client.dto.oauth.naver.NaverUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverOAuthStrategy implements OAuthStrategy {

    private final NaverOauth naverOauth;

    @Override
    public LoginType getType() {
        return LoginType.NAVER;
    }

    @Override
    public String getOauthRedirectURL() {
        return naverOauth.getOauthRedirectURL();
    }

    @Override
    public OAuthProfile getOAuthProfile(String code) {
        NaverOAuthTokenDTO token = naverOauth.requestAccessToken(code);
        NaverUser user = naverOauth.requestUserInfo(token);
        return OAuthProfile.of(
                getType(), user.response().email(), user.response().name(),
                user.response().thumbnail_image());
    }
}
