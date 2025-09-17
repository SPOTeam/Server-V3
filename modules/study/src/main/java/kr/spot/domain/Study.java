package kr.spot.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.spot.base.BaseEntity;
import kr.spot.code.status.ErrorStatus;
import kr.spot.domain.vo.Fee;
import kr.spot.exception.GeneralException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@SQLDelete(sql = "UPDATE study SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Study extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer maxMembers;

    @Column
    private String imageUrl;

    @Embedded
    private Fee fee;

    @Column(length = 1000)
    private String description;

    public static Study of(Long id, String name, Integer maxMembers, String imageUrl, Fee fee, String description) {
        validateStudyNameIsNotBlank(name);
        validateMaxMembers(maxMembers);
        return new Study(id, name, maxMembers, imageUrl, fee, description);
    }

    private static void validateStudyNameIsNotBlank(String name) {
        if (StringUtils.isBlank(name)) {
            throw new GeneralException(ErrorStatus._NAME_CAN_NOT_NULL_OR_EMPTY);
        }
    }

    private static void validateMaxMembers(Integer maxMembers) {
        if (maxMembers != null && maxMembers <= 0) {
            throw new GeneralException(ErrorStatus._MAX_MEMBERS_MUST_BE_POSITIVE);
        }
    }
}

