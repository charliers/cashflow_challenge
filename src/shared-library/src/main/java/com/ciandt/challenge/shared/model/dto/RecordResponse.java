package com.ciandt.challenge.shared.model.dto;

import com.ciandt.challenge.shared.domain.RecordType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record RecordResponse(UUID id, RecordType type, BigDecimal amount, String description, LocalDate refDate,
                             Timestamp createdAt, Timestamp updatedAt, UUID refundToId) {
}
