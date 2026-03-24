package com.ciandt.challenge.command.util;

import com.ciandt.challenge.shared.model.dto.RecordResponse;
import com.ciandt.challenge.shared.domain.FinancialRecord;

public class RecordMapper {

    private RecordMapper() {
    }

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
}
