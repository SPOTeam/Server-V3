package kr.spot.domain.enums;

public enum LoginType {
    KAKAO,
    NAVER;

    public LoginType toLoginType(String provider) {
        return LoginType.valueOf(provider.toUpperCase());
    }
}
