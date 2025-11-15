package kr.spot.infrastructure.jpa.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.spot.domain.QStudy;
import kr.spot.domain.Study;
import kr.spot.domain.associations.QStudyMember;
import kr.spot.domain.enums.StudyMemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory query;

    public List<Study> findMyStudies(Long viewerId, StudyMemberStatus status, Long cursor, int limit) {
        QStudy study = QStudy.study;
        QStudyMember studyMember = QStudyMember.studyMember;

        return query
                .select(study)
                .from(studyMember)
                .join(study).on(study.id.eq(studyMember.studyId))
                .where(
                        studyMember.memberId.eq(viewerId),
                        studyMember.studyMemberStatus.eq(status),
                        ltCursor(cursor, study)
                )
                .orderBy(study.id.desc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression ltCursor(Long cursor, QStudy study) {
        return cursor == null ? null : study.id.lt(cursor);
    }

}
