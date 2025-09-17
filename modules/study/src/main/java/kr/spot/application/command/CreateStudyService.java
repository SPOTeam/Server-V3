package kr.spot.application.command;

import kr.spot.IdGenerator;
import kr.spot.domain.Study;
import kr.spot.domain.associations.StudyCategory;
import kr.spot.domain.associations.StudyRegion;
import kr.spot.domain.associations.StudyStyle;
import kr.spot.domain.vo.Fee;
import kr.spot.infrastructure.jpa.StudyRepository;
import kr.spot.infrastructure.jpa.associations.StudyCategoryRepository;
import kr.spot.infrastructure.jpa.associations.StudyRegionRepository;
import kr.spot.infrastructure.jpa.associations.StudyStyleRepository;
import kr.spot.presentation.dto.request.CreateStudyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateStudyService {

    private final IdGenerator idGenerator;
    private final StudyRepository studyRepository;

    private final StudyStyleRepository studyStyleRepository;
    private final StudyRegionRepository studyRegionRepository;
    private final StudyCategoryRepository studyCategoryRepository;

    public void createStudy(CreateStudyRequest request, Long leaderId) {
        long studyId = idGenerator.nextId();
        Study study = Study.of(studyId, leaderId, request.name(), request.maxMembers(),
                Fee.of(request.hasFee(), request.amount()), request.imageUrl(), request.description());

        studyRepository.save(study);
        saveAllStudyCategories(request, studyId);
        saveAllStudyStyles(request, studyId);
        saveAllStudyRegions(request, studyId);
    }

    private void saveAllStudyCategories(CreateStudyRequest request, long studyId) {
        var studyCategories = request.categories().stream()
                .map(cat -> StudyCategory.of(idGenerator.nextId(), studyId, cat))
                .toList();
        studyCategoryRepository.saveAll(studyCategories);
    }

    private void saveAllStudyStyles(CreateStudyRequest request, long studyId) {
        var studyStyles = request.styles().stream()
                .map(style -> StudyStyle.of(idGenerator.nextId(), studyId, style))
                .toList();
        studyStyleRepository.saveAll(studyStyles);
    }

    private void saveAllStudyRegions(CreateStudyRequest request, long studyId) {
        var studyRegions = request.regionCodes().stream()
                .map(regionCode -> StudyRegion.of(idGenerator.nextId(), studyId, regionCode))
                .toList();
        studyRegionRepository.saveAll(studyRegions);
    }
}
