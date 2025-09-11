package kr.spot.application.oauth.strategy;


import kr.spot.auth.api.dto.OAuthProfile;
import kr.spot.auth.domain.enums.LoginType;

public interface OAuthStrategy {

    LoginType getType();

    String getOauthRedirectURL();

    OAuthProfile getOAuthProfile(String code); // 전략별 구현에서 Member 객체 생성
}
