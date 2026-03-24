package com.ciandt.challenge.shared.mapper;

import com.ciandt.challenge.shared.domain.RecordType;
import com.ciandt.challenge.shared.model.entity.RecordEntity;
import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.google.cloud.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class RecordEntityMapper {

    private RecordEntityMapper() {
    }

    public static RecordEntity toEntity(FinancialRecord domain) {
        RecordEntity entity = new RecordEntity();
        entity.setId(domain.getId());
        entity.setRefDate(domain.getRefDate());
        entity.setType(domain.getType().name());
        entity.setAmount(domain.getAmount());
        entity.setDescription(domain.getDescription());
        entity.setRefundToId(domain.getRefundToId() != null ? domain.getRefundToId() : null);
        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : null);
        //entity.setUpdatedAt(domain.getRefundToId() != null ? domain.getUpdatedAt() : null);
        // updatedAt: Spanner commit timestamp — não precisa setar manualmente
        // quando spannerCommitTimestamp=true, o Spanner preenche no momento do commit
        return entity;
    }

    public static FinancialRecord toDomain(RecordEntity entity) {
        return new FinancialRecord(
                entity.getId(),
                RecordType.valueOf(entity.getType()),
                entity.getAmount(),
                entity.getDescription(),
                entity.getRefDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getRefundToId() != null
                        ? entity.getRefundToId()
                        : null
        );
    }

    private static Date toSpannerDate(LocalDate localDate) {
        return Date.fromYearMonthDay(
                localDate.getYear(),
                localDate.getMonthValue(),
                localDate.getDayOfMonth()
        );
    }

    private static LocalDate toLocalDate(Date spannerDate) {
        return LocalDate.of(
                spannerDate.getYear(),
                spannerDate.getMonth(),
                spannerDate.getDayOfMonth()
        );
    }
}
