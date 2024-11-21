package com.anthony.orders.dtos.responses;

import java.util.Objects;
import java.util.UUID;

/**
 * Status
 */


public class ResponseStatus{

  private String errorCode;
  private String errorMessage;
  private String referenceId;

  private Integer statusCode = 500;

  public ResponseStatus errorCode(String errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * Get errorCode
   * @return errorCode
   **/
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public ResponseStatus errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * Set errorMessage
   **/

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ResponseStatus referenceId(String referenceId) {
    this.referenceId = referenceId;
    return this;
  }

  /**
   * Set referenceId
   **/

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public ResponseStatus statusCode(Integer statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * Get statusCode
   * @return statusCode
   **/
  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }
  
  /**
   * Set error
   **/

  public String setErrorUnauthorized(String message){
    this.referenceId = ResponseStatus.uuid();
    this.errorCode = "AuthenticationError";
    this.errorMessage = message;
    this.statusCode = 401;
    return referenceId;
  }

  public String setErrorValidation(String message){
    this.referenceId = ResponseStatus.uuid();
    this.errorCode = "BadRequest";
    this.errorMessage = message;
    this.statusCode = 400;
    return referenceId;
  }

  public String setErrorNotFound(){
    this.referenceId = ResponseStatus.uuid();
    this.errorCode = "NotFound";
    this.errorMessage = "NotFound";
    this.statusCode = 404;
    return referenceId;
  }

  public String setErrorForbidden(){
    this.referenceId = ResponseStatus.uuid();
    this.errorCode = "Forbidden";
    this.errorMessage = "Forbidden";
    this.statusCode = 403;
    return referenceId;
  }

  public String setErrorConflict(String message){
    this.referenceId = ResponseStatus.uuid();
    this.errorCode = "Conflict";
    this.errorMessage = message;
    this.statusCode = 409;
    return referenceId;
  }

  public String setError(String message){
    this.referenceId = ResponseStatus.uuid();
    this.errorCode = "UnexpectedError";
    this.errorMessage = message;
    this.statusCode = 500;
    return referenceId;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResponseStatus s = (ResponseStatus) o;
    return Objects.equals(this.errorCode, s.errorCode) &&
        Objects.equals(this.errorMessage, s.errorMessage) &&
        Objects.equals(this.referenceId, s.referenceId) &&
        (this.statusCode == s.statusCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorCode, errorMessage, referenceId, statusCode);
  }

  public static String uuid(){
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }
}
