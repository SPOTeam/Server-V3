package kr.spot.auth.api.dto;

import kr.spot.auth.domain.enums.LoginType;

public record OAuthProfile(
        LoginType loginType,
        String email,
        String nickname,
        String profileImageUrl
) {

    public static OAuthProfile of(LoginType loginType, String email, String nickname, String profileImageUrl) {
        return new OAuthProfile(loginType, email, nickname, profileImageUrl);
    }
}