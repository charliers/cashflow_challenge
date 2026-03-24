package com.ciandt.challenge.shared.model.dto;

import com.ciandt.challenge.shared.domain.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record CreateRecordRequest(RecordType type, BigDecimal amount, String description, LocalDate refDate) {
}
