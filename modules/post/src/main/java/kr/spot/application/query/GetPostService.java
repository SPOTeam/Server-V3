package kr.spot.application.query;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.spot.application.ports.PostViewCounter;
import kr.spot.application.ports.ViewAbuseGuard;
import kr.spot.domain.Comment;
import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.infrastructure.jpa.CommentRepository;
import kr.spot.infrastructure.jpa.PostRepository;
import kr.spot.infrastructure.jpa.PostStatsRepository;
import kr.spot.infrastructure.jpa.querydsl.PostQueryRepository;
import kr.spot.presentation.query.dto.response.GetPostDetailResponse;
import kr.spot.presentation.query.dto.response.GetPostListResponse;
import kr.spot.presentation.query.dto.response.GetPostStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPostService {

    public static final int MAX_PAGE_SIZE = 50;
    public static final int MAX_CONTENT_LENGTH = 100;
    private final PostViewCounter postViewCounter;
    private final ViewAbuseGuard viewAbuseGuard;

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final CommentRepository commentRepository;
    private final PostStatsRepository postStatsRepository;

    public GetPostListResponse getPostList(Long cursor, Long viewerId, Integer size) {
        final int pageSize = Math.min(size, MAX_PAGE_SIZE);

        // size+1 로 읽어 hasNext 판별
        List<Post> rows = postQueryRepository.findPageByIdDesc(cursor, pageSize + 1);

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }
        Long nextCursor = hasNext ? rows.getLast().getId() : null;

        List<Long> ids = rows.stream().map(Post::getId).toList();

        Map<Long, PostStats> stats = postQueryRepository.findStatsByPostIds(ids);
        Set<Long> liked = postQueryRepository.findLikedPostIds(viewerId, ids);

        List<GetPostListResponse.PostListResponse> posts = rows.stream()
                .map(p -> {
                    PostStats st = stats.get(p.getId());
                    long view = st.getViewCount();
                    long like = st.getLikeCount();
                    long comment = st.getCommentCount();

                    return GetPostListResponse.PostListResponse.of(
                            p.getId(),
                            p.getTitle(),
                            summarize(p.getContent()),
                            new GetPostStatsResponse(like, view, comment),
                            p.getCreatedAt(),
                            liked.contains(p.getId())
                    );
                })
                .toList();

        return GetPostListResponse.builder()
                .posts(posts)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    private long getTotalViewCount(Long viewerId, Post p, PostStats st) {
        return st.getViewCount() + getViewDelta(p.getId(), viewerId);
    }

    private String summarize(String content) {
        content = content.strip();
        return content.length() > MAX_CONTENT_LENGTH ? content.substring(0, MAX_CONTENT_LENGTH) + "..." : content;
    }

    public GetPostDetailResponse getPostDetail(Long postId, Long viewerId) {
        Post post = postRepository.getPostById(postId);
        PostStats postStats = postStatsRepository.getPostStatsById(postId);
        List<Comment> comments = commentRepository.getCommentsByPostId(postId);

        long displayView = getTotalViewCount(viewerId, post, postStats);

        return toResponse(post, postStats, displayView, comments);
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

    private static GetPostDetailResponse toResponse(Post post, PostStats postStats, long viewCount,
                                                    List<Comment> comments) {
        return GetPostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType())
                .writer(GetPostDetailResponse.WriterInfoResponse.of(
                        post.getWriterInfo().getWriterId(),
                        post.getWriterInfo().getWriterName(),
                        post.getWriterInfo().getWriterProfileImageUrl()))
                .stats(GetPostStatsResponse.from(
                        postStats.getLikeCount(), viewCount, postStats.getCommentCount()))
                .createdAt(post.getCreatedAt())
                .comments(
                        comments.stream()
                                .map(comment -> GetPostDetailResponse.CommentResponse.of(
                                        comment.getId(),
                                        comment.getContent(),
                                        GetPostDetailResponse.WriterInfoResponse.of(
                                                comment.getWriterInfo().getWriterId(),
                                                comment.getWriterInfo().getWriterName(),
                                                comment.getWriterInfo().getWriterProfileImageUrl()
                                        ),
                                        comment.getCreatedAt()
                                ))
                                .toList()
                )
                .commentCount(comments.size())
                .build();
    }
}
