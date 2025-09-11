package kr.spot.auth.api;

import kr.spot.auth.api.dto.TokenDTO;

public interface TokenPort {
    TokenDTO createToken(Long memberId);

    void validateToken(String token);

    Long getMemberIdByToken(String token);
}
