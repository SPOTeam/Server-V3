package kr.spot.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.spot.ApiResponse;
import kr.spot.annotations.CurrentMember;
import kr.spot.application.command.CreateStudyService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.presentation.dto.request.CreateStudyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyCommandController {

    private final CreateStudyService createStudyService;

    @Tag(name = "스터디")
    @Operation(summary = "스터디 생성", description = "새로운 스터디를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createStudy(@RequestBody CreateStudyRequest request,
                                                         @CurrentMember @Parameter(hidden = true) Long memberId) {
        createStudyService.createStudy(request, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }
}
