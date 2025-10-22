package kr.spot.application.query;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.spot.application.ports.HotPostStore;
import kr.spot.application.ports.PostViewCounter;
import kr.spot.application.ports.ViewAbuseGuard;
import kr.spot.domain.Comment;
import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.domain.enums.PostType;
import kr.spot.infrastructure.jpa.CommentRepository;
import kr.spot.infrastructure.jpa.PostRepository;
import kr.spot.infrastructure.jpa.PostStatsRepository;
import kr.spot.infrastructure.jpa.querydsl.PostQueryRepository;
import kr.spot.presentation.query.dto.response.PostDetailResponse;
import kr.spot.presentation.query.dto.response.PostListResponse;
import kr.spot.presentation.query.dto.response.PostListResponse.PostList;
import kr.spot.presentation.query.dto.response.PostOverviewResponse;
import kr.spot.presentation.query.dto.response.PostOverviewResponse.PostOverview;
import kr.spot.presentation.query.dto.response.PostStatsResponse;
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
    private final HotPostStore hotPostStore;

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final CommentRepository commentRepository;
    private final PostStatsRepository postStatsRepository;

    /**
     * 게시글 목록을 커서 기반 페이지네이션으로 조회합니다.
     *
     * @param postType 게시글 유형
     * @param cursor   이전 페이지의 마지막 게시글 ID (다음 페이지 조회를 위한 커서)
     * @param viewerId 현재 조회자 ID
     * @param size     요청 페이지 크기
     * @return 게시글 목록 및 페이지네이션 정보
     */
    public PostListResponse getPostList(PostType postType, Long cursor, Long viewerId, Integer size) {
        final int pageSize = Math.min(size, MAX_PAGE_SIZE);

        List<Post> rows = postQueryRepository.findPageByIdDesc(postType, cursor, pageSize + 1);
        boolean hasNext = rows.size() > pageSize;

        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }
        Long nextCursor = hasNext ? rows.getLast().getId() : null;

        List<Long> ids = extractPostIds(rows);

        Map<Long, PostStats> stats = postQueryRepository.findStatsByPostIds(ids);
        Set<Long> liked = postQueryRepository.findLikedPostIds(viewerId, ids);

        List<PostList> posts = mapPostsToResponseList(rows, stats, liked);

        return buildGetPostListResponse(posts, hasNext, nextCursor);
    }

    /**
     * 특정 게시글의 상세 정보를 조회합니다.
     *
     * @param postId   게시글 ID
     * @param viewerId 현재 조회자 ID
     * @return 게시글 상세 정보 (Post, PostStats, Comments 포함)
     */
    public PostDetailResponse getPostDetail(Long postId, Long viewerId) {
        Post post = postRepository.getPostById(postId);
        PostStats postStats = postStatsRepository.getPostStatsById(postId);
        List<Comment> comments = commentRepository.getCommentsByPostId(postId);

        // 실시간 뷰 카운트 계산
        long displayView = calculateTotalViewCount(viewerId, post, postStats);

        return mapToPostDetailResponse(post, postStats, displayView, comments);
    }


    /**
     * 인기 게시글 상위 3개를 조회합니다.
     *
     * @return 인기 게시글 3개의 개요 정보
     */
    public PostOverviewResponse getHotPosts() {
        List<Long> top3 = hotPostStore.getTop3();
        if (!top3.isEmpty()) {
            List<Post> posts = postRepository.getPostsByIds(top3);
            Map<Long, PostStats> stats = postQueryRepository.findStatsByPostIds(top3);

            List<PostOverview> hotPosts = posts.stream()
                    .map(p -> mapToPostOverviewResponse(p, stats.get(p.getId())))
                    .toList();

            return PostOverviewResponse.of(hotPosts);
        }
        return null;
    }

    private PostOverview mapToPostOverviewResponse(Post p, PostStats st) {
        return PostOverview.of(
                p.getId(),
                p.getTitle(),
                createContentSummary(p.getContent()),
                st.getCommentCount(),
                p.getPostType()
        );
    }

    // ------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // ------------------------------------------------------------------------

    private PostListResponse buildGetPostListResponse(List<PostList> posts, boolean hasNext,
                                                      Long nextCursor) {
        return PostListResponse.builder()
                .posts(posts)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    private long calculateTotalViewCount(Long viewerId, Post p, PostStats st) {
        return st.getViewCount() + getViewDeltaFromCounter(p.getId(), viewerId);
    }

    private String createContentSummary(String content) {
        content = content.strip();
        return content.length() > MAX_CONTENT_LENGTH ? content.substring(0, MAX_CONTENT_LENGTH) + "..." : content;
    }

    private long getViewDeltaFromCounter(Long postId, Long viewerId) {
        long viewDelta = 0L;
        try {
            if (viewAbuseGuard.shouldCount(postId, viewerId)) {
                viewDelta = postViewCounter.incrementAndGetDelta(postId); // 델타 증가 및 현재값 반환
            } else {
                viewDelta = postViewCounter.currentDelta(postId); // 델타만 조회
            }
        } catch (Exception ignore) {
            // Redis 장애 시에도 조회가 가능하도록 예외 무시: 표시값은 DB 기준
            log.warn("Redis view counter access failed for postId: {}", postId, ignore);
        }
        return viewDelta;
    }

    private static List<Long> extractPostIds(List<Post> rows) {
        return rows.stream().map(Post::getId).toList();
    }

    private List<PostList> mapPostsToResponseList(List<Post> rows, Map<Long, PostStats> stats,
                                                  Set<Long> liked) {
        return rows.stream()
                .map(p -> mapToPostListResponse(p, stats, liked))
                .toList();
    }

    private PostDetailResponse mapToPostDetailResponse(Post post, PostStats postStats, long viewCount,
                                                       List<Comment> comments) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType())
                .writer(PostDetailResponse.WriterInfoResponse.of(
                        post.getWriterInfo().getWriterId(),
                        post.getWriterInfo().getWriterName(),
                        post.getWriterInfo().getWriterProfileImageUrl()))
                .stats(PostStatsResponse.from(
                        postStats.getLikeCount(), viewCount, postStats.getCommentCount()))
                .createdAt(post.getCreatedAt())
                .comments(
                        comments.stream()
                                .map(comment -> PostDetailResponse.CommentResponse.of(
                                        comment.getId(),
                                        comment.getContent(),
                                        PostDetailResponse.WriterInfoResponse.of(
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

    private PostList mapToPostListResponse(Post p, Map<Long, PostStats> stats, Set<Long> liked) {
        PostStats st = stats.get(p.getId());
        long view = st.getViewCount();
        long like = st.getLikeCount();
        long comment = st.getCommentCount();

        return PostList.of(
                p.getId(),
                p.getTitle(),
                createContentSummary(p.getContent()), // 요약 메서드 이름 변경 적용
                new PostStatsResponse(like, view, comment),
                p.getCreatedAt(),
                liked.contains(p.getId())
        );
    }
}