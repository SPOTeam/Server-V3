package kr.spot.code.status;

import kr.spot.code.BaseErrorCode;
import kr.spot.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //공통 에러
    _INTERNAL_SERVER_ERROR(500, "COMMON500", "서버 내부 오류 발생"),
    _BAD_REQUEST(404, "COMMON4000", "잘못된 요청입니다."),
    _UNAUTHORIZED(401, "COMMON4001", "인증되지 않은 요청입니다."),
    _FORBIDDEN(403, "COMMON4002", "접근이 거부되었습니다."),
    ;

    private final int httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build()
                ;
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
