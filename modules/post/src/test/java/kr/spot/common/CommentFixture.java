package kr.spot.common;

import kr.spot.domain.Comment;
import kr.spot.domain.vo.WriterInfo;

public class CommentFixture {
    // 공통 픽스처 값
    public final Long WRITER_ID = 100L;
    public final Long OTHER_ID = 200L;
    public final Long POST_ID = 10L;
    public final Long COMMENT_ID = 999L;
    public final String CONTENT = "hello world";

    public static Comment of(Long commentId, Long postId, Long writerId, String content) {
        WriterInfo wi = WriterInfo.of(writerId, "nick-" + writerId, "img-" + writerId);
        return Comment.of(commentId, postId, wi, content);
    }

}
