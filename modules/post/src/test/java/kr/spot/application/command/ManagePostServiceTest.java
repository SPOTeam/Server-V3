package kr.spot.application.command;

import static kr.spot.common.PostFixture.CONTENT;
import static kr.spot.common.PostFixture.OTHER_WRITER_ID;
import static kr.spot.common.PostFixture.POST_ID;
import static kr.spot.common.PostFixture.TITLE;
import static kr.spot.common.PostFixture.writerInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import kr.spot.IdGenerator;
import kr.spot.common.PostFixture;
import kr.spot.domain.Post;
import kr.spot.domain.enums.PostType;
import kr.spot.domain.vo.WriterInfo;
import kr.spot.exception.GeneralException;
import kr.spot.infrastructure.PostRepository;
import kr.spot.infrastructure.PostStatsRepository;
import kr.spot.ports.GetWriterInfoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagePostServiceTest {

    @Mock
    IdGenerator idGenerator;

    @Mock
    GetWriterInfoPort getWriterInfoPort;

    @Mock
    PostRepository postRepository;

    @Mock
    PostStatsRepository postStatsRepository;

    ManagePostService managePostService;

    @BeforeEach
    void setUp() {
        managePostService = new ManagePostService(idGenerator, getWriterInfoPort, postRepository, postStatsRepository);
    }

    @Test
    @DisplayName("게시글을 정상적으로 생성할 수 있다.")
    void should_create_post_successfully() {
        // given
        WriterInfo writerInfo = writerInfo();

        // when
        Post post = Post.of(POST_ID, writerInfo, TITLE, CONTENT, PostType.ALL);

        // then
        assertThat(post).isNotNull();
        assertThat(post.getId()).isEqualTo(POST_ID);
        assertThat(post.getTitle()).isEqualTo(TITLE);
        assertThat(post.getContent()).isEqualTo(CONTENT);
        assertThat(post.getPostType()).isEqualTo(PostType.ALL);
    }

    @Test
    @DisplayName("게시글 작성자가 아닌 경우 게시글 수정에 실패한다.")
    void should_fail_to_update_post_when_not_writer() {
        // given
        WriterInfo writerInfo = writerInfo();
        Post post = Post.of(POST_ID, writerInfo, TITLE, CONTENT, PostType.ALL);

        when(postRepository.getPostByIdWithLock(POST_ID)).thenReturn(post);

        // when & then
        assertThatThrownBy(() ->
                managePostService.updatePost(POST_ID, PostFixture.updatePostRequest(), OTHER_WRITER_ID)
        ).isInstanceOf(GeneralException.class);
    }

    @Test
    @DisplayName("게시글 작성자가 아닌 경우 게시글 삭제에 실패한다.")
    void should_fail_to_delete_post_when_not_writer() {
        // given
        WriterInfo writerInfo = writerInfo();
        Post post = Post.of(POST_ID, writerInfo, TITLE, CONTENT, PostType.ALL);

        when(postRepository.getPostByIdWithLock(POST_ID)).thenReturn(post);

        // when & then
        assertThatThrownBy(() ->
                managePostService.deletePost(POST_ID, OTHER_WRITER_ID)
        ).isInstanceOf(GeneralException.class);
    }
}