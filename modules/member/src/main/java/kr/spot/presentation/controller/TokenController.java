package kr.spot.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.spot.ApiResponse;
import kr.spot.auth.api.TokenPort;
import kr.spot.auth.api.dto.TokenDTO;
import kr.spot.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class TokenController {

    private final TokenPort tokenPort;

    /* ----------------------------- JWT 토큰 관리 API ------------------------------------- */

    @Tag(name = "회원 관리 API", description = "회원 관리 API")
    @Operation(summary = "[세션 유지] 액세스 토큰 재발급 API",
            description = """
                    ## [세션 유지] 액세스 토큰을 재발급 하는 API입니다.
                    리프레시 토큰을 통해 액세스 토큰을 재발급 합니다.
                    리프레시 토큰의 만료 기간 이전인 경우에만 재발급이 가능합니다.
                    액세스 토큰을 재발급 하는 경우, 리프레시 토큰도 재발급 됩니다.
                    """)
    @PostMapping("/reissue")
    public ApiResponse<TokenDTO> reissueToken(@RequestHeader("refreshToken") String refreshToken) {
        return ApiResponse.onSuccess(SuccessStatus._CREATED,
                tokenPort.createToken(tokenPort.getMemberIdByToken(refreshToken)));
    }

}
