package kr.spot.presentation.query.dto.response;

public record GetPostStatsResponse(
        Long likeCount,
        Long viewCount,
        Long commentCount
) {
    public static GetPostStatsResponse from(Long likeCount, Long viewCount, Long commentCount) {
        return new GetPostStatsResponse(likeCount, viewCount, commentCount);
    }
}