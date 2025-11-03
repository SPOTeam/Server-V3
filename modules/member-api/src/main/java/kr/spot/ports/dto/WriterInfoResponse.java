package kr.spot.ports.dto;

public record WriterInfoResponse(
        Long writerId,
        String nickname,
        String profileImageUrl
) {

    public static WriterInfoResponse of(Long writerId, String nickname, String profileImageUrl) {
        return new WriterInfoResponse(writerId, nickname, profileImageUrl);
    }
}
