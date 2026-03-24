package com.ciandt.challenge.shared.model.entity;

import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import lombok.*;
import lombok.extern.java.Log;

import java.math.BigDecimal;
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
@Log
public class RecordEntity {

    /**
     * UUID do lançamento — segunda coluna da PK composta.
     * Armazenado como STRING(36) no Spanner.
     */
    @PrimaryKey()
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
    @Column(name = "refdate")
    private Date refDate;

    /**
     * Spanner TIMESTAMP com allow_commit_timestamp=false.
     */
    @Column(name = "createdat", spannerCommitTimestamp = false)
    private Timestamp createdAt;

    @Column(name = "updatedat", spannerCommitTimestamp = false)
    private Timestamp updatedAt;

    @Column(name = "refundtoid")
    private UUID refundToId;
}
