package com.ciandt.challenge.command.repository;

import com.ciandt.challenge.shared.model.entity.RecordEntity;
import com.google.cloud.Date;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.google.cloud.spring.data.spanner.repository.query.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordSpannerRepository
        extends SpannerRepository<RecordEntity, UUID> {

    /**
     * Busca por id apenas (sem refDate) — para o endpoint GET /records/{id}.
     */
    //@Query("SELECT * FROM records WHERE id = @id LIMIT 1")
    Optional<RecordEntity> findById(@Param("id") UUID id);

    /**
     * Listagem paginada com filtros opcionais por período e tipo.
     * O token de paginação é baseado em (ref_date, id) da última linha retornada.
     */
    @Query("""
            SELECT * FROM records
            WHERE (@dateFrom IS NULL OR ref_date >= @dateFrom)
              AND (@dateTo   IS NULL OR ref_date <= @dateTo)
              AND (@type     IS NULL OR type = @type)
            ORDER BY ref_date DESC, id ASC
            LIMIT @pageSize
            """)
    List<RecordEntity> listRecords(
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("type") String type,
            @Param("pageSize") int pageSize
    );

    /**
     * Contagem total para o mesmo conjunto de filtros (sem paginação).
     */
    @Query("""
            SELECT COUNT(*) FROM records
            WHERE (@dateFrom IS NULL OR ref_date >= @dateFrom)
              AND (@dateTo   IS NULL OR ref_date <= @dateTo)
              AND (@type     IS NULL OR type = @type)
            """)
    long countRecords(
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("type") String type
    );

    /**
     * Verifica se já existe um estorno vinculado ao lançamento original.
     * Um lançamento está estornado quando outro registro aponta refund_to_id para ele.
     */
    @Query("""
            SELECT COUNT(*) FROM records
            WHERE refund_to_id = @originalId
            LIMIT 1
            """)
    long countRefundsByOriginalId(@Param("originalId") String originalId);
}
