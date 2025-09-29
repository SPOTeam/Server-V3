package kr.spot.presentation.query;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.spot.ApiResponse;
import kr.spot.annotations.CurrentMember;
import kr.spot.application.query.GetMemberInfoService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.presentation.query.dto.response.GetMemberNameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberQueryController {

    private final GetMemberInfoService getMemberInfoService;

    @Operation(summary = "회원 이름 조회", description = "현재 로그인한 회원의 이름을 조회합니다.")
    @GetMapping("/name")
    public ResponseEntity<ApiResponse<GetMemberNameResponse>> getMemberName(
            @CurrentMember @Parameter(hidden = true) Long memberId) {
        GetMemberNameResponse response = getMemberInfoService.getMemberName(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, response));
    }
}
