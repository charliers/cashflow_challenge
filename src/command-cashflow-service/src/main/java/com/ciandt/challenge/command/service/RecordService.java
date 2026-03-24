package com.ciandt.challenge.command.service;

import com.ciandt.challenge.command.repository.RecordSpannerRepository;
import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.ciandt.challenge.shared.domain.RecordType;
import com.ciandt.challenge.shared.model.entity.RecordEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j
public class RecordService implements com.ciandt.challenge.command.iface.RecordService {

    @Autowired
    private RecordSpannerRepository repository;

    public RecordService(){}

    @Override
    public void createRecord(FinancialRecord financialRecord) {

        log.info("Creating record for financial record: " + financialRecord);

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

        log.info("Saving record for financial record: " + newRecord);
        repository.save(newRecord);

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
