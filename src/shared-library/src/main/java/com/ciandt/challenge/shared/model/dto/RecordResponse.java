package com.ciandt.challenge.shared.model.dto;

import com.ciandt.challenge.shared.domain.RecordType;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordResponse(UUID id, RecordType type, BigDecimal amount, String description, Date refDate,
                             Timestamp createdAt, Timestamp updatedAt, UUID refundToId) {
}
