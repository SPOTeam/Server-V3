package kr.spot.domain.vo;

import jakarta.persistence.Embeddable;
import kr.spot.code.status.ErrorStatus;
import kr.spot.exception.GeneralException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Fee {

    private boolean hasFee;

    private Integer amount;

    public static Fee of(boolean hasFee, Integer amount) {
        if (!hasFee) {
            amount = 0;
        } else {
            if (amount == null || amount < 0) {
                throw new GeneralException(ErrorStatus._INVALID_FEE_AMOUNT);
            }
        }
        return new Fee(hasFee, amount);
    }
}
