package kr.spot.application.command;

import kr.spot.IdGenerator;
import kr.spot.domain.Post;
import kr.spot.domain.PostStats;
import kr.spot.domain.vo.WriterInfo;
import kr.spot.infrastructure.PostRepository;
import kr.spot.infrastructure.PostStatsRepository;
import kr.spot.ports.GetWriterInfoPort;
import kr.spot.ports.dto.WriterInfoResponse;
import kr.spot.presentation.dto.request.ManagePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagePostService {

    private final IdGenerator idGenerator;
    private final GetWriterInfoPort getWriterInfoPort;

    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;

    public long createPost(ManagePostRequest request, Long writerId) {
        WriterInfo writerInfo = getWriterInfo(writerId);
        Post post = createAndSavePost(request, writerInfo);
        initializeAndSavePostStats(post);
        return post.getId();
    }

    public void updatePost(Long postId, ManagePostRequest request, Long writerId) {
        Post post = getPostWithLock(postId);
        post.update(request.title(), request.content(), request.postType(), writerId);
    }

    public void deletePost(Long postId, Long writerId) {
        Post post = getPostWithLock(postId);
        post.delete(writerId);
    }

    private Post getPostWithLock(Long postId) {
        return postRepository.getPostByIdWithLock(postId);
    }

    private WriterInfo getWriterInfo(Long writerId) {
        WriterInfoResponse writerInfoResponse = getWriterInfoPort.get(writerId);
        return WriterInfo.of(writerInfoResponse.writerId(), writerInfoResponse.nickname(),
                writerInfoResponse.profileImageUrl());
    }

    private Post createAndSavePost(ManagePostRequest request, WriterInfo writerInfo) {
        Post post = Post.of(idGenerator.nextId(), writerInfo, request.title(), request.content(), request.postType());
        postRepository.save(post);
        return post;
    }

    private void initializeAndSavePostStats(Post post) {
        PostStats postStats = PostStats.of(post.getId());
        postStatsRepository.save(postStats);
    }
}
