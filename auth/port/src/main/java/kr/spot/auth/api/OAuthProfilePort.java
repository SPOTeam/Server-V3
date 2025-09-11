package kr.spot.auth.api;

import kr.spot.auth.api.dto.OAuthProfile;
import kr.spot.auth.domain.enums.LoginType;

public interface OAuthProfilePort {

    String redirectURL(LoginType loginType);

    OAuthProfile getOAuthProfile(LoginType loginType, String code);
}
