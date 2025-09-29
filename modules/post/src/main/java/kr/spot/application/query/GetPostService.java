package kr.spot.application.query;

import kr.spot.application.ports.PostViewCounter;
import kr.spot.application.ports.ViewAbuseGuard;
import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.infrastructure.jpa.PostRepository;
import kr.spot.infrastructure.jpa.PostStatsRepository;
import kr.spot.presentation.command.dto.response.GetPostDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPostService {

    private final PostViewCounter postViewCounter;
    private final ViewAbuseGuard viewAbuseGuard;

    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;

    public GetPostDetailResponse getPostDetail(Long postId, Long viewerId) {
        Post post = postRepository.getPostById(postId);
        PostStats postStats = postStatsRepository.getPostStatsById(postId);

        long displayView = postStats.getViewCount() + getViewDelta(postId, viewerId);

        return toResponse(post, postStats, displayView);
    }

    private long getViewDelta(Long postId, Long viewerId) {
        long viewDelta = 0L;
        try {
            if (viewAbuseGuard.shouldCount(postId, viewerId)) {
                viewDelta = postViewCounter.incrementAndGetDelta(postId); // 델타 현재값
            } else {
                viewDelta = postViewCounter.currentDelta(postId); // 델타만 조회
            }
        } catch (Exception ignore) {
            // Redis 장애 시에도 조회는 계속: 표시값은 DB 기준
        }
        return viewDelta;
    }

    private static GetPostDetailResponse toResponse(Post post, PostStats postStats, long viewCount) {
        return GetPostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType())
                .writer(GetPostDetailResponse.WriterInfoResponse.of(
                        post.getWriterInfo().getWriterId(),
                        post.getWriterInfo().getWriterName(),
                        post.getWriterInfo().getWriterProfileImageUrl()))
                .stats(GetPostDetailResponse.GetPostStatsResponse.from(
                        postStats.getLikeCount(), viewCount, postStats.getCommentCount()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
