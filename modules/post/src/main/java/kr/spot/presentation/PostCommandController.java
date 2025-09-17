package kr.spot.presentation;


import io.swagger.v3.oas.annotations.Parameter;
import kr.spot.ApiResponse;
import kr.spot.annotations.CurrentMember;
import kr.spot.application.command.ManagePostService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.presentation.dto.request.ManageCommentRequest;
import kr.spot.presentation.dto.request.ManagePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostCommandController {

    private final ManagePostService postService;


    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> createPost(
            @RequestBody ManagePostRequest request,
            @CurrentMember @Parameter(hidden = true) Long writerId
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessStatus._CREATED, postService.createPost(request, writerId)));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestBody ManagePostRequest request,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        postService.updatePost(postId, request, writerId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        postService.deletePost(postId, writerId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable Long postId,
            @RequestBody ManageCommentRequest request,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody ManageCommentRequest request,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @CurrentMember @Parameter(hidden = true) Long writerId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }
}
