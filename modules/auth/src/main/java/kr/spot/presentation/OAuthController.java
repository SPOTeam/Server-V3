package kr.spot.presentation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.spot.ApiResponse;
import kr.spot.application.oauth.OAuthService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.domain.enums.LoginType;
import kr.spot.presentation.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @Tag(name = "소셜 로그인 API")
    @Operation(summary = "소셜 로그인 리다이렉트 URL 반환",
            description = "소셜 로그인 타입에 따라 리다이렉트 URL을 반환합니다. " +
                    "예: /api/oauth/redirect-url/naver")
    @GetMapping("/redirect-url/{type}")
    public ResponseEntity<ApiResponse<String>> getRedirectUrl(@PathVariable("type") String type) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK,
                oAuthService.redirectURL(LoginType.valueOf(type.toUpperCase()))));
    }

    @Tag(name = "소셜 로그인 API")
    @Operation(summary = "소셜 로그인 콜백 처리",
            description = "소셜 로그인 후 받은 code를 통해 로그인 또는 회원가입을 처리합니다. " +
                    "예: /api/oauth/callback/naver?code=YOUR_CODE")
    @GetMapping("/callback/{type}")
    public ResponseEntity<ApiResponse<TokenDTO>> socialLoginCallback(
            @PathVariable("type") String type,
            @RequestParam("code") String code) {

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK,
                oAuthService.getOAuthProfile(LoginType.valueOf(type.toUpperCase()), code)));
    }
}