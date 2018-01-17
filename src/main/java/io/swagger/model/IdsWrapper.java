package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * IdsWrapper
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-08T23:11:14.377Z")

public class IdsWrapper {
  @JsonProperty("ids")
  private List<IdWrapper> ids = new ArrayList<IdWrapper>();

  public IdsWrapper ids(List<IdWrapper> ids) {
    this.ids = ids;
    return this;
  }

  public IdsWrapper addIdsItem(IdWrapper idsItem) {
    this.ids.add(idsItem);
    return this;
  }

   /**
   * Get ids
   * @return ids
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<IdWrapper> getIds() {
    return ids;
  }

  public void setIds(List<IdWrapper> ids) {
    this.ids = ids;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IdsWrapper idsWrapper = (IdsWrapper) o;
    return Objects.equals(this.ids, idsWrapper.ids);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ids);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IdsWrapper {\n");
    
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

