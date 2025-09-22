package kr.spot.presentation;

import kr.spot.ApiResponse;
import kr.spot.application.token.TokenReissueService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.presentation.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenReissueService tokenReissueService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenDTO>> reissueToken(
            @RequestHeader String refreshToken
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessStatus._OK, tokenReissueService.reissueToken(refreshToken)));
    }
}
