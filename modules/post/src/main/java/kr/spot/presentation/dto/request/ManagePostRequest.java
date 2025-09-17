package kr.spot.presentation.dto.request;

import kr.spot.domain.enums.PostType;

public record ManagePostRequest(
        String title, String content, PostType postType
) {
}
