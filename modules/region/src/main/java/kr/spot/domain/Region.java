package kr.spot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.Immutable;

@Getter
@Entity
@Immutable
@Table(name = "region",
        indexes = {
                @Index(name = "ux_region_code", columnList = "code", unique = true),
                @Index(name = "ix_region_names", columnList = "province,district,neighborhood")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Region {

    @Id
    private Long id;

    @NaturalId
    @Column(nullable = false, updatable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 50)
    private String province;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(nullable = false, length = 50)
    private String neighborhood;

    public static Region of(Long id, String code, String province, String district, String neighborhood) {
        return new Region(id, code, province, district, neighborhood);
    }
}
