package kr.spot.annotations;

public interface EnsureMemberFromOAuthPort {
    long ensure(String provider, String email, String nickname, String imageUrl);
}