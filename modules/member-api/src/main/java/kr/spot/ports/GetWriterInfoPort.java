package kr.spot.ports;

import kr.spot.ports.dto.WriterInfoResponse;

public interface GetWriterInfoPort {
    WriterInfoResponse get(long memberId);

}
