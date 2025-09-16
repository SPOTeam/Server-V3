package kr.spot.ports;

public interface EnsureMemberFromOAuthPort {
    long ensure(String provider, String email, String nickname, String imageUrl);
}