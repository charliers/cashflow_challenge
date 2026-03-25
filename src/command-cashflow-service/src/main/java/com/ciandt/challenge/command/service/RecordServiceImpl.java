package com.ciandt.challenge.command.service;

import com.ciandt.challenge.command.config.PubSubConfig;
import com.ciandt.challenge.command.iface.RecordSpannerCustomRepository;
import com.ciandt.challenge.command.iface.RecordSpannerRepository;
import com.ciandt.challenge.command.repository.RecordSpannerCustomRepositoryImpl;
import com.ciandt.challenge.command.util.Tools;
import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.ciandt.challenge.shared.domain.RecordType;
import com.ciandt.challenge.shared.mapper.RecordMapper;
import com.ciandt.challenge.shared.model.entity.RecordEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j
public class RecordService implements com.ciandt.challenge.command.iface.RecordService {

    private RecordSpannerCustomRepository customRepository;
    private RecordSpannerRepository repository;

    @Autowired
    private PubSubConfig.PubSubOutboundGateway messagingGateway;

    public RecordService(RecordSpannerCustomRepository customRepository,
                         RecordSpannerRepository repository){
        this.customRepository = customRepository;
        this.repository = repository;
    }

    @Override
    public void createRecord(FinancialRecord financialRecord) {

        RecordEntity newRecord = RecordMapper.toEntity(financialRecord);
        repository.save(newRecord);
        messagingGateway.sendToPubSub(Tools.toJson(financialRecord));

    }

    @Override
    public List<FinancialRecord> listRecords(LocalDate dateFrom, LocalDate dateTo, RecordType type, Integer pageSize) {
        return customRepository.listRecords(
                dateFrom,
                dateTo,
                type.toString(),
                pageSize )
                .stream()
                .map(RecordMapper::toFinancialRecord)
                .collect(Collectors.toList());
    }

    @Override
    public long countRecords(LocalDate dateFrom, LocalDate dateTo, RecordType type) {
        return 0;
    }

    @Override
    public Optional<FinancialRecord> getById(UUID id) {
        return repository.findById(id)
                .map(RecordMapper::toFinancialRecord);
    }

    @Override
    public RefundResult refundRecord(LocalDate refDate, UUID id, BigDecimal refundAmount, String description) {
        return null;
    }
}
