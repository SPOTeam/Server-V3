package kr.spot.presentation.query.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GetPostListResponse(
        List<PostListResponse> posts,
        boolean hasNext,
        Long nextCursor
) {

    public record PostListResponse(
            Long postId,
            String title,
            String content,
            GetPostStatsResponse stats,
            LocalDateTime createdAt,
            Boolean isLiked
    ) {
        public static PostListResponse of(Long postId, String title, String content,
                                          GetPostStatsResponse stats, LocalDateTime createdAt,
                                          Boolean isLiked) {
            return new PostListResponse(postId, title, content, stats, createdAt, isLiked);
        }
    }
}