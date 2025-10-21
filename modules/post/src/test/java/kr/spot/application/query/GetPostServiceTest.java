package kr.spot.application.query;

import static kr.spot.common.CommentFixture.comments;
import static kr.spot.common.PostFixture.post;
import static kr.spot.common.PostFixture.postStats;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import kr.spot.application.ports.PostViewCounter;
import kr.spot.application.ports.ViewAbuseGuard;
import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.exception.GeneralException;
import kr.spot.infrastructure.jpa.CommentRepository;
import kr.spot.infrastructure.jpa.PostRepository;
import kr.spot.infrastructure.jpa.PostStatsRepository;
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
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostStatsRepository postStatsRepository;

    GetPostService getPostService;

    @BeforeEach
    void setUp() {
        getPostService = new GetPostService(postViewCounter, viewAbuseGuard, postRepository, commentRepository,
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
}