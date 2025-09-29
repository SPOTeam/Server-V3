package kr.spot.presentation.dto.request;

import java.util.List;

public record RegisterPreferredCategoryRequest(
        List<String> categories
) {
}
