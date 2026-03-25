package com.ciandt.challenge.command.repository;

import com.ciandt.challenge.shared.model.entity.RecordEntity;
import com.google.cloud.Date;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.google.cloud.spring.data.spanner.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordSpannerRepository
        extends SpannerRepository<RecordEntity, UUID> {

    Optional<RecordEntity> findById(@Param("id") UUID id);
    List<RecordEntity> listRecords(
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("type") String type,
            @Param("pageSize") int pageSize
    );
    long countRecords(
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("type") String type
    );

    long countRefundsByOriginalId(@Param("originalId") String originalId);
}
