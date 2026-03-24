package com.ciandt.challenge.shared.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipo do lançamento financeiro: - `CREDIT`: entrada de recursos - `DEBIT`: saída de recursos 
 */
public enum RecordType {
  CREDIT("CREDIT"),
    DEBIT("DEBIT");

  private final String value;

  RecordType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static RecordType fromValue(String text) {
    for (RecordType b : RecordType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
