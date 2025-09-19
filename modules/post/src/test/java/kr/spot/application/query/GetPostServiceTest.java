package kr.spot.application.query;

import static kr.spot.common.PostFixture.post;
import static kr.spot.common.PostFixture.postStats;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.exception.GeneralException;
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
    PostRepository postRepository;

    @Mock
    PostStatsRepository postStatsRepository;

    GetPostService getPostService;

    @BeforeEach
    void setUp() {
        getPostService = new GetPostService(postRepository, postStatsRepository);
    }

    @Test
    @DisplayName("게시글 상세 조회를 정상적으로 수행할 수 있다.")
    void should_get_post_detail_successfully() {
        // given
        long postId = 1L;
        Post post = post();
        PostStats postStats = postStats();

        when(postRepository.getPostById(postId)).thenReturn(post);
        when(postStatsRepository.getPostStatsById(postId)).thenReturn(postStats);

        // when
        var response = getPostService.getPostDetail(postId);

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

        when(postRepository.getPostById(postId)).thenThrow(
                new GeneralException(ErrorStatus._POST_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> getPostService.getPostDetail(postId))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 stats을 조회할 경우 예외가 발생한다.")
    void should_throw_exception_when_post_stats_not_found() {
        // given
        long postId = 999L;

        when(postRepository.getPostById(postId)).thenReturn(post());
        when(postStatsRepository.getPostStatsById(postId)).thenThrow(
                new GeneralException(ErrorStatus._POST_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> getPostService.getPostDetail(postId))
                .isInstanceOf(GeneralException.class);
    }
}