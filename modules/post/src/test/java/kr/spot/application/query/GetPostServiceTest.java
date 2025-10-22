package kr.spot.application.query;

import static kr.spot.common.CommentFixture.comments;
import static kr.spot.common.PostFixture.post;
import static kr.spot.common.PostFixture.postStats;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import kr.spot.application.ports.HotPostStore;
import kr.spot.application.ports.PostViewCounter;
import kr.spot.application.ports.ViewAbuseGuard;
import kr.spot.code.status.ErrorStatus;
import kr.spot.common.PostFixture;
import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.domain.enums.PostType;
import kr.spot.exception.GeneralException;
import kr.spot.infrastructure.jpa.CommentRepository;
import kr.spot.infrastructure.jpa.PostRepository;
import kr.spot.infrastructure.jpa.PostStatsRepository;
import kr.spot.infrastructure.jpa.querydsl.PostQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPostServiceTest {

    @Mock
    PostViewCounter postViewCounter;

    @Mock
    ViewAbuseGuard viewAbuseGuard;

    @Mock
    HotPostStore hotPostStore;

    @Mock
    PostRepository postRepository;

    @Mock
    PostQueryRepository postQueryRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostStatsRepository postStatsRepository;

    GetPostService getPostService;

    @BeforeEach
    void setUp() {
        getPostService = new GetPostService(postViewCounter, viewAbuseGuard, hotPostStore, postRepository,
                postQueryRepository,
                commentRepository,
                postStatsRepository);
    }

    @Test
    @DisplayName("게시글 상세 조회를 정상적으로 수행할 수 있다.")
    void should_get_post_detail_successfully() {
        // given
        long postId = 1L;
        long viewerId = 2L;
        Post post = post();
        PostStats postStats = postStats();

        when(postRepository.getPostById(postId)).thenReturn(post);
        when(postStatsRepository.getPostStatsById(postId)).thenReturn(postStats);

        // when
        var response = getPostService.getPostDetail(postId, viewerId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.postId()).isEqualTo(post.getId());
        assertThat(response.title()).isEqualTo(post.getTitle());
        assertThat(response.content()).isEqualTo(post.getContent());
        assertThat(response.postType()).isEqualTo(post.getPostType());

        assertThat(response.stats().likeCount()).isEqualTo(postStats.getLikeCount());
        assertThat(response.stats().viewCount()).isEqualTo(postStats.getViewCount());
        assertThat(response.stats().commentCount()).isEqualTo(postStats.getCommentCount());
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 조회할 경우 예외가 발생한다.")
    void should_throw_exception_when_post_not_found() {
        // given
        long postId = 999L;
        long viewerId = 2L;

        when(postRepository.getPostById(postId)).thenThrow(
                new GeneralException(ErrorStatus._POST_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> getPostService.getPostDetail(postId, viewerId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 stats을 조회할 경우 예외가 발생한다.")
    void should_throw_exception_when_post_stats_not_found() {
        // given
        long postId = 999L;
        long viewerId = 2L;

        when(postRepository.getPostById(postId)).thenReturn(post());
        when(postStatsRepository.getPostStatsById(postId)).thenThrow(
                new GeneralException(ErrorStatus._POST_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> getPostService.getPostDetail(postId, viewerId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("댓글이 있는 경우에는 댓글도 함께 조회된다.")
    void should_get_post_with_comments() {
        // given
        long postId = 1L;
        long viewerId = 2L;
        Post post = post();
        PostStats postStats = postStats();

        when(postRepository.getPostById(postId)).thenReturn(post);
        when(postStatsRepository.getPostStatsById(postId)).thenReturn(postStats);
        when(commentRepository.getCommentsByPostId(postId)).thenReturn(comments());

        // when
        var response = getPostService.getPostDetail(postId, viewerId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.commentCount()).isEqualTo(3);
        assertThat(response.comments()).isNotNull();
        assertThat(response.comments().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글이 없는 경우에도 정상적으로 조회된다.")
    void should_get_post_without_comments() {
        // given
        long postId = 1L;
        long viewerId = 2L;
        Post post = post();
        PostStats postStats = postStats();

        when(postRepository.getPostById(postId)).thenReturn(post);
        when(postStatsRepository.getPostStatsById(postId)).thenReturn(postStats);
        when(commentRepository.getCommentsByPostId(postId)).thenReturn(
                java.util.Collections.emptyList());

        // when
        var response = getPostService.getPostDetail(postId, viewerId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.commentCount()).isEqualTo(0);
        assertThat(response.comments()).isNotNull();
        assertThat(response.comments().size()).isEqualTo(0);
    }

    // ---------------- 추가: 뷰 델타/가드/장애 케이스 ----------------

    @Test
    @DisplayName("어뷰징 가드 통과 시: incrementAndGetDelta를 호출해 DB viewCount + 델타로 노출")
    void should_add_delta_when_guard_allows() {
        long postId = 1L, viewerId = 10L;
        Post post = post();
        PostStats stats = postStats(); // 예: DB viewCount = 0L 가정
        when(postRepository.getPostById(postId)).thenReturn(post);
        when(postStatsRepository.getPostStatsById(postId)).thenReturn(stats);

        when(viewAbuseGuard.shouldCount(postId, viewerId)).thenReturn(true);
        when(postViewCounter.incrementAndGetDelta(postId)).thenReturn(5L);

        var res = getPostService.getPostDetail(postId, viewerId);

        verify(viewAbuseGuard).shouldCount(postId, viewerId);
        verify(postViewCounter).incrementAndGetDelta(postId);
        assertThat(res.stats().viewCount()).isEqualTo(stats.getViewCount() + 5L);
    }

    @Test
    @DisplayName("어뷰징 가드 차단 시: currentDelta만 조회해 DB viewCount + 델타로 노출")
    void should_use_current_delta_when_guard_blocks() {
        long postId = 1L, viewerId = 10L;
        Post post = post();
        PostStats stats = postStats(); // DB viewCount = 0L 가정
        when(postRepository.getPostById(postId)).thenReturn(post);
        when(postStatsRepository.getPostStatsById(postId)).thenReturn(stats);

        when(viewAbuseGuard.shouldCount(postId, viewerId)).thenReturn(false);
        when(postViewCounter.currentDelta(postId)).thenReturn(7L);

        var res = getPostService.getPostDetail(postId, viewerId);

        verify(viewAbuseGuard).shouldCount(postId, viewerId);
        verify(postViewCounter).currentDelta(postId);
        assertThat(res.stats().viewCount()).isEqualTo(stats.getViewCount() + 7L);
    }

    @Test
    @DisplayName("레디스 장애 시: 예외를 삼키고 DB viewCount만 노출(가용성 우선)")
    void should_fallback_to_db_count_when_redis_fails() {
        long postId = 1L, viewerId = 10L;
        Post post = post();
        PostStats stats = postStats(); // DB viewCount 예: 123L
        when(postRepository.getPostById(postId)).thenReturn(post);
        when(postStatsRepository.getPostStatsById(postId)).thenReturn(stats);

        when(viewAbuseGuard.shouldCount(postId, viewerId)).thenReturn(true);
        when(postViewCounter.incrementAndGetDelta(postId)).thenThrow(new RuntimeException("Redis down"));

        var res = getPostService.getPostDetail(postId, viewerId);

        verify(viewAbuseGuard).shouldCount(postId, viewerId);
        verify(postViewCounter).incrementAndGetDelta(postId);
        assertThat(res.stats().viewCount()).isEqualTo(stats.getViewCount()); // 델타 미반영
    }

    // ---------------- getPostList ----------------

    @Test
    @DisplayName("다음 페이지가 있는 경우: 요청 size보다 1개 많은 데이터를 조회하여 hasNext=true와 nextCursor를 올바르게 반환한다")
    void should_return_post_list_with_next_page() {
        // given
        long viewerId = 1L;
        int size = 5;
        List<Post> posts = IntStream.range(0, size + 1)
                .mapToObj(i -> PostFixture.post((long) (10 - i)))
                .toList();
        Map<Long, PostStats> statsMap = new LinkedHashMap<>();
        for (Post p : posts) {
            statsMap.put(p.getId(), PostStats.of(p.getId()));
        }

        when(postQueryRepository.findPageByIdDesc(null, null, size + 1)).thenReturn(posts);
        when(postQueryRepository.findStatsByPostIds(any())).thenReturn(statsMap);
        when(postQueryRepository.findLikedPostIds(eq(viewerId), any())).thenReturn(Set.of());

        // when
        var response = getPostService.getPostList(null, null, viewerId, size);

        // then
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursor()).isEqualTo(posts.get(size - 1).getId());
        assertThat(response.posts()).hasSize(size);
    }

    @Test
    @DisplayName("마지막 페이지인 경우: 요청 size보다 작은 데이터를 조회하여 hasNext=false와 nextCursor=null을 반환한다")
    void should_return_post_list_without_next_page() {
        // given
        long viewerId = 1L;
        int size = 5;
        List<Post> posts = IntStream.range(0, size)
                .mapToObj(i -> PostFixture.post((long) (10 - i)))
                .toList();

        Map<Long, PostStats> statsMap = new LinkedHashMap<>();
        for (Post p : posts) {
            statsMap.put(p.getId(), PostStats.of(p.getId()));
        }

        when(postQueryRepository.findPageByIdDesc(null, null, size + 1)).thenReturn(posts);
        when(postQueryRepository.findStatsByPostIds(any())).thenReturn(statsMap);
        when(postQueryRepository.findLikedPostIds(eq(viewerId), any())).thenReturn(Set.of());

        // when
        var response = getPostService.getPostList(null, null, viewerId, size);

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursor()).isNull();
        assertThat(response.posts()).hasSize(size);
    }

    @Test
    @DisplayName("게시글의 내용이 너무 길 경우, 요약하여 반환한다")
    void should_summarize_content_if_too_long() {
        // given
        long viewerId = 1L;
        int size = 1;
        Post longContentPost = Post.of(1L, PostFixture.writerInfo(), "title", "a".repeat(200), PostType.COUNSELING);

        when(postQueryRepository.findPageByIdDesc(null, null, size + 1)).thenReturn(List.of(longContentPost));
        when(postQueryRepository.findStatsByPostIds(any())).thenReturn(Map.of(1L, PostStats.of(1L)));
        when(postQueryRepository.findLikedPostIds(eq(viewerId), any())).thenReturn(Set.of());

        // when
        var response = getPostService.getPostList(null, null, viewerId, size);

        // then
        String summarizedContent = response.posts().get(0).content();
        assertThat(summarizedContent).hasSize(GetPostService.MAX_CONTENT_LENGTH + 3);
        assertThat(summarizedContent).endsWith("...");
    }
}
