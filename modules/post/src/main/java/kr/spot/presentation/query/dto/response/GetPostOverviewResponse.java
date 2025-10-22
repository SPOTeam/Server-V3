package kr.spot.presentation.query.dto.response;

import java.util.List;
import kr.spot.domain.enums.PostType;

public record GetPostOverviewResponse(
        List<PostOverviewResponse> hotPosts
) {

    public record PostOverviewResponse(
            Long postId,
            String title,
            String content,
            Long commentCount,
            PostType postType
    ) {
        public static PostOverviewResponse of(Long postId, String title, String content,
                                              Long commentCount, PostType postType) {
            return new PostOverviewResponse(postId, title, content, commentCount, postType);
        }
    }

    public static GetPostOverviewResponse of(List<PostOverviewResponse> hotPosts) {
        return new GetPostOverviewResponse(hotPosts);
    }
}
