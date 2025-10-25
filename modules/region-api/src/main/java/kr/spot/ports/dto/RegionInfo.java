package kr.spot.ports.dto;

public record RegionInfo(
        String province,
        String district,
        String neighborhood
) {

    public static RegionInfo of(String province, String district, String neighborhood) {
        return new RegionInfo(province, district, neighborhood);
    }
}
