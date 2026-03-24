package com.ciandt.challenge.command.controller;

import com.ciandt.challenge.command.service.RecordService;
import com.ciandt.challenge.shared.mapper.RecordMapper;
import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.ciandt.challenge.shared.domain.RecordType;
import com.ciandt.challenge.shared.model.dto.CreateRecordRequest;
import com.ciandt.challenge.shared.model.dto.PaginationResponse;
import com.ciandt.challenge.shared.model.dto.RecordListResponse;
import com.ciandt.challenge.shared.model.dto.RecordResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cashflow/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<RecordResponse> createRecord(
            @Valid @RequestBody CreateRecordRequest request
    ) {

        FinancialRecord financialRecord = RecordMapper.toFinancialRecord(request);

        recordService.createRecord(financialRecord);
        RecordResponse response = RecordMapper.toResponse(financialRecord);

        financialRecord = null;

        // OpenAPI sugere 201 com corpo; Location pode ser adicionada se desejar
        // URI location = URI.create("/cashflow/records/" + created.getId());
        //return ResponseEntity.created(location).body(response);
        return ResponseEntity.created(null).body(response);
    }

    @GetMapping
    public ResponseEntity<RecordListResponse> listRecords(
            @RequestParam(name = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(name = "type", required = false) RecordType type,
            @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        List<FinancialRecord> financialRecords = recordService.listRecords(dateFrom, dateTo, type, pageSize);
        
        List<RecordResponse> data = financialRecords.stream()
                .map(RecordMapper::toResponse)
                .collect(Collectors.toList());

        PaginationResponse pagination = new PaginationResponse(pageSize, ((long) financialRecords.size()));

        RecordListResponse response = new RecordListResponse(data, pagination);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordResponse> getRecord(
            @PathVariable("id") UUID id
    ) {
        return recordService.getById(id)
                .map(financialRecord -> ResponseEntity.ok(RecordMapper.toResponse(financialRecord)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
