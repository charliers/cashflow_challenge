package com.ciandt.challenge.shared.model.dto;

import com.ciandt.challenge.shared.domain.RecordType;

import java.math.BigDecimal;
import com.google.cloud.Date;

public record CreateRecordRequest(RecordType type, BigDecimal amount, String description, Date refDate) {
}
