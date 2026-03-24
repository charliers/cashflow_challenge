package com.ciandt.challenge.shared.model.entity;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.cloud.Date;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidade mapeada para a tabela 'records' no Google Cloud Spanner.
 * PK composta: (ref_date, id) — conforme notas arquiteturais da spec OpenAPI.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "records")
public class RecordEntity {

    /**
     * UUID do lançamento — segunda coluna da PK composta.
     * Armazenado como STRING(36) no Spanner.
     */
    @PrimaryKey(keyOrder = 1)
    @Column(name = "id")
    private UUID id;

    @Column(name = "type")
    private String type;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    /**
     * Data de referência
     * Usamos com.google.cloud.Date pois é o tipo nativo do Spanner para DATE.
     */
    @Column(name = "ref_date")
    private Date refDate;

    /**
     * Spanner TIMESTAMP com allow_commit_timestamp=false.
     */
    @Column(name = "created_at", spannerCommitTimestamp = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", spannerCommitTimestamp = true)
    private OffsetDateTime updatedAt;

    @Column(name = "refund_to_id")
    private UUID refundToId;
}
