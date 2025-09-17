package kr.spot.presentation.dto.response;

public record CreatePostResponse(Long postId) {

    public static CreatePostResponse from(Long postId) {
        return new CreatePostResponse(postId);
    }
}
