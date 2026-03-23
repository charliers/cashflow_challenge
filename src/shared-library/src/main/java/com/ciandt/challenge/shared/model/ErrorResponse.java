package com.ciandt.challenge.shared.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

/**
 * ErrorResponse
 */
@Validated
@Setter
@Getter
public class ErrorResponse  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("code")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String code = null;

  @JsonProperty("message")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String message = null;

  @JsonProperty("details")
  private List<ErrorResponseDetails> details = null;

  public ErrorResponse(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public ErrorResponse addDetailsItem(ErrorResponseDetails detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<ErrorResponseDetails>();
    }
    this.details.add(detailsItem);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
