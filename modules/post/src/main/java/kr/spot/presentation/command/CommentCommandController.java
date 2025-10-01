package kr.spot.presentation.command;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.spot.ApiResponse;
import kr.spot.annotations.CurrentMember;
import kr.spot.application.command.ManageCommentService;
import kr.spot.code.status.SuccessStatus;
import kr.spot.presentation.command.dto.request.ManageCommentRequest;
import kr.spot.presentation.command.dto.response.CreateCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "댓글")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentCommandController {

    private final ManageCommentService commentService;

    @Operation(summary = "댓글 작성", description = "특정 게시글에 새로운 댓글을 작성합니다.")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CreateCommentResponse>> createComment(
            @CurrentMember @Parameter(hidden = true) Long writerId,
            @PathVariable Long postId,
            @RequestBody ManageCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED,
                commentService.createComment(writerId, postId, request)));
    }

    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다.")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody ManageCommentRequest request,
            @CurrentMember @Parameter(hidden = true) Long writerId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @Operation(summary = "댓글 삭제", description = "기존 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @CurrentMember @Parameter(hidden = true) Long writerId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

}
