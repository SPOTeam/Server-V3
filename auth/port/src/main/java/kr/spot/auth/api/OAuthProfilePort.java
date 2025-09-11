package kr.spot.auth.api;

import kr.spot.auth.api.dto.OAuthProfile;
import kr.spot.auth.domain.enums.LoginType;

public interface OAuthProfilePort {
    OAuthProfile getOAuthProfile(LoginType loginType, String code);
}
