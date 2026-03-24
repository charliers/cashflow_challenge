package com.ciandt.challenge.shared.model.dto;

import java.math.BigDecimal;

public record RefundRecordRequest(BigDecimal amount, String description) {
}
