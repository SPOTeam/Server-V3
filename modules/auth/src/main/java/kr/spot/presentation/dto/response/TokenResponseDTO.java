package kr.spot.presentation.dto.response;

public record TokenResponseDTO(boolean isSpotMember, String accessToken, String refreshToken,
                               Long accessTokenExpiresIn) {

    public static TokenResponseDTO of(boolean isSpotMember, String accessToken, String refreshToken,
                                      Long accessTokenExpiresIn) {
        return new TokenResponseDTO(isSpotMember, accessToken, refreshToken, accessTokenExpiresIn);
    }
}
