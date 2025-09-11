package kr.spot.application.token;


import kr.spot.presentation.dto.response.TokenResponseDTO;

public interface TokenReissueService {

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급
    TokenResponseDTO reissueToken(String refreshToken);
}
