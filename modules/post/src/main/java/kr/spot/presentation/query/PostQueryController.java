package kr.spot.presentation.query;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kr.spot.ApiResponse;
import kr.spot.annotations.CurrentMember;
import kr.spot.application.query.GetPostService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.domain.enums.PostType;
import kr.spot.presentation.query.dto.response.GetPostDetailResponse;
import kr.spot.presentation.query.dto.response.GetPostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게시글")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostQueryController {

    private final GetPostService getPostService;

    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @GetMapping("{postId}")
    public ResponseEntity<ApiResponse<GetPostDetailResponse>> getPostDetail(
            @PathVariable Long postId,
            @CurrentMember @Parameter(hidden = true) Long viewerId
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessStatus._OK, getPostService.getPostDetail(postId, viewerId)));
    }

    @Operation(summary = "게시글 리스트 조회", description = "게시글 리스트를 조회합니다. 마지막으로 본 게시글 이후의 게시글들을 페이징하여 가져옵니다."
            + "게시글 유형별로 필터링이 가능합니다. 아무 조건을 입력하지 않은 경우, 전체 유형을 대상으로 조회합니다. "
            + "또한 글자 수가 많은 게시글의 경우 일부 내용이 생략되어 제공됩니다. (현재 기준은 100자)")
    @GetMapping
    public ResponseEntity<ApiResponse<GetPostListResponse>> getPostList(
            @CurrentMember @Parameter(hidden = true) Long viewerId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) PostType postType,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer size
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessStatus._OK, getPostService.getPostList(postType, cursor, viewerId, size)));
    }
}
