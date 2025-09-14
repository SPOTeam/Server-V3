package kr.spot.base.enums;

public enum LoginType {
    KAKAO,
    NAVER;

    public LoginType toLoginType(String provider) {
        return LoginType.valueOf(provider.toUpperCase());
    }
}
