package com.ciandt.challenge.command.iface;

import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.ciandt.challenge.shared.domain.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecordService {

    void createRecord(FinancialRecord financialRecord);

    List<FinancialRecord> listRecords(LocalDate dateFrom,
                                      LocalDate dateTo,
                                      RecordType type,
                                      Integer pageSize);

    long countRecords(LocalDate dateFrom,
                      LocalDate dateTo,
                      RecordType type);

    Optional<FinancialRecord> getById(UUID id);

    /**
     * Cria estorno e retorna par (refund, originalAtualizado)
     */
    RefundResult refundRecord(LocalDate refDate,
                              UUID id,
                              BigDecimal refundAmount,
                              String description);

    record RefundResult(FinancialRecord refund, FinancialRecord original) {
    }
}
