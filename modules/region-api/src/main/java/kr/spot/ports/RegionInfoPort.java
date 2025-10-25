package kr.spot.ports;

import kr.spot.ports.dto.RegionInfo;

public interface RegionInfoPort {

    boolean isValidRegionCode(String regionCode);

    RegionInfo getRegionInfoByCode(String regionCode);
}
