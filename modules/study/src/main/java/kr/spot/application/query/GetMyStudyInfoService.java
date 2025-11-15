package kr.spot.application.query;

import java.util.List;
import kr.spot.application.mapper.StudyDTOMapper;
import kr.spot.domain.Study;
import kr.spot.domain.enums.StudyMemberStatus;
import kr.spot.infrastructure.jpa.querydsl.StudyQueryRepository;
import kr.spot.presentation.query.dto.response.GetStudyOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMyStudyInfoService {
    public static final int MAX_PAGE_SIZE = 50;

    private final StudyQueryRepository studyQueryRepository;

    public GetStudyOverviewResponse getMyStudyOverview(Long viewerId, StudyMemberStatus status, Long cursor,
                                                       int size) {
        final int pageSize = Math.min(size, MAX_PAGE_SIZE);
        List<Study> rows = studyQueryRepository.findMyStudies(viewerId, status, cursor, pageSize + 1);
        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }
        Long nextCursor = hasNext ? rows.getLast().getId() : null;
        return StudyDTOMapper.toDTO(rows, hasNext, nextCursor);
    }
}
