package kr.spot.presentation.query.dto.response;

import java.util.List;

public record GetStudyOverviewResponse(
        List<StudyOverview> content,
        long totalElements,
        int page,
        int size,
        int totalPages
) {
    public static GetStudyOverviewResponse of(List<StudyOverview> content, long totalElements, int page,
                                              int size) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        return new GetStudyOverviewResponse(content, totalElements, page, size, totalPages);
    }

    public record StudyOverview(
            String title,
            String description,
            int totalMembers,
            int currentMembers,
            long likeCount,
            boolean isLiked,
            long hitCount,
            String profileImageUrl
    ) {

        public static StudyOverview of(
                String title,
                String description,
                int totalMembers,
                int currentMembers,
                long likeCount,
                boolean isLiked,
                long hitCount,
                String profileImageUrl
        ) {
            return new StudyOverview(
                    title,
                    description,
                    totalMembers,
                    currentMembers,
                    likeCount,
                    isLiked,
                    hitCount,
                    profileImageUrl
            );
        }

    }
}
