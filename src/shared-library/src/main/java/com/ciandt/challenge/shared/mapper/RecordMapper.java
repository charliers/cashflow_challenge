package com.ciandt.challenge.shared.mapper;

import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.ciandt.challenge.shared.domain.RecordType;
import com.ciandt.challenge.shared.model.dto.CreateRecordRequest;
import com.ciandt.challenge.shared.model.dto.RecordResponse;
import com.ciandt.challenge.shared.model.entity.RecordEntity;
import com.google.cloud.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class RecordMapper {

    public static RecordResponse toResponse(FinancialRecord financialRecord) {
        if (financialRecord == null) {
            return null;
        }
        return new RecordResponse(financialRecord.getId(),
                                financialRecord.getType(),
                                financialRecord.getAmount(),
                                financialRecord.getDescription(),
                                financialRecord.getRefDate(),
                                financialRecord.getCreatedAt(),
                                financialRecord.getUpdatedAt(),
                                financialRecord.getRefundToId() );
    }

    public static RecordEntity toEntity(FinancialRecord financialRecord) {
        if (financialRecord == null) {
            return null;
        }
        return new RecordEntity(
                financialRecord.getId(),
                financialRecord.getType().name(),
                financialRecord.getAmount(),
                financialRecord.getDescription(),
                toSpannerDate(financialRecord.getRefDate()),
                Timestamp.of(financialRecord.getCreatedAt()),
                Timestamp.of(financialRecord.getUpdatedAt()),
                financialRecord.getRefundToId()
        );
    }

    public static FinancialRecord toFinancialRecord(RecordEntity recordEntity) {
        if (recordEntity == null) {
            return null;
        }
        return new FinancialRecord(
                recordEntity.getId(),
                RecordType.valueOf(recordEntity.getType()),
                recordEntity.getAmount(),
                recordEntity.getDescription(),
                toLocalDate(recordEntity.getRefDate()),
                recordEntity.getCreatedAt().toSqlTimestamp(),
                recordEntity.getUpdatedAt().toSqlTimestamp(),
                recordEntity.getRefundToId()
        );
    }

    public static FinancialRecord toFinancialRecord(CreateRecordRequest recordRequest) {
        if (recordRequest == null) {
            return null;
        }

        java.sql.Timestamp now = java.sql.Timestamp.from(Instant.now());

        return new FinancialRecord(
                UUID.randomUUID(),
                recordRequest.type(),
                recordRequest.amount(),
                recordRequest.description(),
                recordRequest.refDate(),
                now,
                now,
                null
        );
    }

    private static com.google.cloud.Date toSpannerDate(LocalDate date) {
        //return com.google.cloud.Date.fromJavaUtilDate(date);
        return com.google.cloud.Date.fromYearMonthDay(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth()
        );
    }

    private static LocalDate toLocalDate(com.google.cloud.Date spannerDate) {
        return LocalDate.of(
                spannerDate.getYear(),
                spannerDate.getMonth(),
                spannerDate.getDayOfMonth()
        );
    }
}
