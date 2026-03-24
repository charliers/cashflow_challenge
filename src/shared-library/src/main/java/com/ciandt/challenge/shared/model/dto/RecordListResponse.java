package com.ciandt.challenge.shared.model.dto;

import java.util.List;

public record RecordListResponse(List<RecordResponse> data, PaginationResponse pagination) {
}
