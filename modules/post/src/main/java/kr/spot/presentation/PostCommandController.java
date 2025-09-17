package kr.spot.presentation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.spot.ApiResponse;
import kr.spot.annotations.CurrentMember;
import kr.spot.application.command.ManagePostService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.presentation.dto.request.ManagePostRequest;
import kr.spot.presentation.dto.response.CreatePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게시글")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostCommandController {

    private final ManagePostService postService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePostResponse>> createPost(
            @RequestBody ManagePostRequest request,
            @CurrentMember @Parameter(hidden = true) Long writerId
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessStatus._CREATED, postService.createPost(request, writerId)));
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestBody ManagePostRequest request,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        postService.updatePost(postId, request, writerId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._NO_CONTENT));
    }

    @Operation(summary = "게시글 삭제", description = "기존 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        postService.deletePost(postId, writerId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._NO_CONTENT));
    }

    @Operation(summary = "게시글 좋아요", description = "특정 게시글에 좋아요를 추가합니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        postService.likePost(postId, writerId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._NO_CONTENT));
    }

    @Operation(summary = "게시글 좋아요", description = "특정 게시글에 좋아요를 추가합니다.")
    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @PathVariable Long postId,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        postService.unlikePost(postId, writerId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._NO_CONTENT));
    }
}
