package kr.spot.application.query;

import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.infrastructure.jpa.PostRepository;
import kr.spot.infrastructure.jpa.PostStatsRepository;
import kr.spot.presentation.dto.response.GetPostDetailResponse;
import kr.spot.presentation.dto.response.GetPostDetailResponse.WriterInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPostService {

    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;

    public GetPostDetailResponse getPostDetail(Long postId) {
        var post = postRepository.getPostById(postId);
        var postStats = postStatsRepository.getPostStatsById(postId);

        return getPostDetailResponse(post, postStats);
    }

    private static GetPostDetailResponse getPostDetailResponse(Post post, PostStats postStats) {
        return GetPostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType())
                .writer(WriterInfoResponse.of(
                        post.getWriterInfo().getWriterId(),
                        post.getWriterInfo().getWriterName(),
                        post.getWriterInfo().getWriterProfileImageUrl()
                ))
                .stats(
                        GetPostDetailResponse.GetPostStatsResponse.from(
                                postStats.getLikeCount(),
                                postStats.getViewCount(),
                                postStats.getCommentCount()
                        )
                )
                .createdAt(post.getCreatedAt())
                .build();
    }
}
