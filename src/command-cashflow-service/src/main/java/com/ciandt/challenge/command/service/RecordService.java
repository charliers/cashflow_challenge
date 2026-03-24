package com.ciandt.challenge.command.service;

import com.ciandt.challenge.command.repository.RecordSpannerRepository;
import com.ciandt.challenge.shared.domain.RecordType;
import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.ciandt.challenge.shared.model.entity.RecordEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RecordService implements com.ciandt.challenge.command.iface.RecordService {

    private final RecordSpannerRepository repository;

    public RecordService(RecordSpannerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createRecord(FinancialRecord financialRecord) {

        RecordEntity newRecord = new RecordEntity(
                financialRecord.getId(),
                financialRecord.getType().name(),
                financialRecord.getAmount(),
                financialRecord.getDescription(),
                financialRecord.getRefDate(),
                financialRecord.getCreatedAt(),
                financialRecord.getUpdatedAt(),
                null
        );

    }

    @Override
    public List<FinancialRecord> listRecords(LocalDate dateFrom, LocalDate dateTo, RecordType type, Integer pageSize) {
        return List.of();
    }

    @Override
    public long countRecords(LocalDate dateFrom, LocalDate dateTo, RecordType type) {
        return 0;
    }

    @Override
    public Optional<FinancialRecord> getById(UUID id) {
        return Optional.empty();
    }

    @Override
    public RefundResult refundRecord(LocalDate refDate, UUID id, BigDecimal refundAmount, String description) {
        return null;
    }
}
