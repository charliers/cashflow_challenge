package com.ciandt.challenge.shared.model.dto;

import java.util.List;

public record ErrorResponse(String code, String message, List<ErrorDetail> details) {
}
