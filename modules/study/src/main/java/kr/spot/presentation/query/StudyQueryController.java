package kr.spot.presentation.query;

import io.swagger.v3.oas.annotations.Operation;
import kr.spot.ApiResponse;
import kr.spot.code.status.SuccessStatus;
import kr.spot.presentation.query.dto.response.GetStudyOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyQueryController {

    // 내가 모집중인 스터디
    @Operation(summary = "내가 모집중인 스터디 조회",
            description = "내가 모집중인 스터디를 조회합니다. (page, size 파라미터로 페이징 처리 가능)")
    @GetMapping("/me/recruiting")
    public ResponseEntity<ApiResponse<GetStudyOverviewResponse>> getMyRecruitedStudies(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, null));
    }

    @Operation(summary = "대기중인 스터디 조회",
            description = "대기중인 스터디를 조회합니다. (page, size 파라미터로 페이징 처리 가능)"
                    + "현재 신청 기능이 없으므로 빈 응답을 반환합니다.")
    @GetMapping("/me/waiting")
    public ResponseEntity<ApiResponse<GetStudyOverviewResponse>> getMyWaitingStudies(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, null));
    }

    @Operation(summary = "신청한 스터디 조회",
            description = "내가 신청한 스터디를 조회합니다. (page, size 파라미터로 페이징 처리 가능) "
                    + "현재 신청 기능이 없으므로 빈 응답을 반환합니다.")
    @GetMapping("/me/applied")
    public ResponseEntity<ApiResponse<GetStudyOverviewResponse>> getMyAppliedStudies(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, null));
    }

    @Operation(summary = "지금 가장 인기있는 스터디 조회",
            description = "지금 가장 인기있는 스터디를 조회합니다. (page, size 파라미터로 페이징 처리 가능)")
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<GetStudyOverviewResponse>> getHotStudies(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, null));
    }

    @Operation(summary = "추천하는 스터디 조회",
            description = "추천하는 스터디를 조회합니다. (page, size 파라미터로 페이징 처리 가능)")
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<GetStudyOverviewResponse>> getRecommendedStudies(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, null));
    }

    // 필터링 O
    // 내 지역 스터디 - 아무 값 안넣으면 이 회원의 전체 관심 지역 스터디로 조회
    // 내 관심사 스터디 - 아무 값 안넣으면 이 회원의 전체 관심사 스터디로 조회
    // 모집중 스터디
    // 전체 스터디 조회
}
