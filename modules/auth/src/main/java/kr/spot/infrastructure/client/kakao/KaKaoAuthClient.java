package kr.spot.infrastructure.client.kakao;


import static kr.spot.infrastructure.constants.AuthConstants.GRANT_TYPE_AUTHORIZATION_CODE;
import static kr.spot.infrastructure.constants.AuthConstants.HEADER_CONTENT_TYPE;

import kr.spot.presentation.dto.oauth.kakao.KaKaoOAuthTokenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoAuthClient", url = "https://kauth.kakao.com")
public interface KaKaoAuthClient {

    @PostMapping("/oauth/token")
    KaKaoOAuthTokenDTO getKaKaoAccessToken(
            @RequestHeader(HEADER_CONTENT_TYPE) String contentType,
            @RequestParam String grant_type,
            @RequestParam String redirectUri,
            @RequestParam String client_id,
            @RequestParam(defaultValue = GRANT_TYPE_AUTHORIZATION_CODE) String code);
}
