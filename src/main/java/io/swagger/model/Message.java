package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Message
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-08T23:11:14.377Z")

public class Message   {
  @JsonProperty("recipients")
  private List<IdWrapper> recipients = new ArrayList<IdWrapper>();

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("sender")
  private Integer sender = null;

  @JsonProperty("read_by")
  private List<IdWrapper> readBy = null;

  public Message recipients(List<IdWrapper> recipients) {
    this.recipients = recipients;
    return this;
  }

  public Message addRecipientsItem(IdWrapper recipientsItem) {
    this.recipients.add(recipientsItem);
    return this;
  }

   /**
   * Get recipients
   * @return recipients
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<IdWrapper> getRecipients() {
    return recipients;
  }

  public void setRecipients(List<IdWrapper> recipients) {
    this.recipients = recipients;
  }

  public Message message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Get message
   * @return message
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Message id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Message sender(Integer sender) {
    this.sender = sender;
    return this;
  }

   /**
   * Get sender
   * @return sender
  **/
  @ApiModelProperty(value = "")


  public Integer getSender() {
    return sender;
  }

  public void setSender(Integer sender) {
    this.sender = sender;
  }

  public Message readBy(List<IdWrapper> readBy) {
    this.readBy = readBy;
    return this;
  }

  public Message addReadByItem(IdWrapper readByItem) {
    if (this.readBy == null) {
      this.readBy = new ArrayList<IdWrapper>();
    }
    this.readBy.add(readByItem);
    return this;
  }

   /**
   * Get readBy
   * @return readBy
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<IdWrapper> getReadBy() {
    return readBy;
  }

  public void setReadBy(List<IdWrapper> readBy) {
    this.readBy = readBy;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(this.recipients, message.recipients) &&
        Objects.equals(this.message, message.message) &&
        Objects.equals(this.id, message.id) &&
        Objects.equals(this.sender, message.sender) &&
        Objects.equals(this.readBy, message.readBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipients, message, id, sender, readBy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Message {\n");
    
    sb.append("    recipients: ").append(toIndentedString(recipients)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    sender: ").append(toIndentedString(sender)).append("\n");
    sb.append("    readBy: ").append(toIndentedString(readBy)).append("\n");
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

