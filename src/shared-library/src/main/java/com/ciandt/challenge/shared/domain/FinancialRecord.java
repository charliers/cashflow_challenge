package com.ciandt.challenge.shared.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@Data
public class FinancialRecord implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("id")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private UUID id = null;

  @JsonProperty("type")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private RecordType type = null;

  @JsonProperty("amount")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private BigDecimal amount = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("refDate")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private LocalDate refDate = null;

  @JsonProperty("createdAt")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private OffsetDateTime createdAt = null;

  @JsonProperty("updatedAt")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private OffsetDateTime updatedAt = null;

  @JsonProperty("refundToId")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  private UUID refundToId = null;

}
