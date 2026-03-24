package com.ciandt.challenge.shared.model.dto;

import com.ciandt.challenge.shared.domain.RecordType;
import com.google.cloud.Date;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RecordResponse(UUID id, RecordType type, BigDecimal amount, String description, Date refDate,
                             OffsetDateTime createdAt, OffsetDateTime updatedAt, UUID refundToId) {
}
