package com.hcl.model;

public class ErrorResponse {
 private String errorCode; 
 private String message;   
 private String details;   // Optional

 public ErrorResponse() {
 }

 public ErrorResponse(String errorCode, String message) {
     this.errorCode = errorCode;
     this.message = message;
 }

 public ErrorResponse(String errorCode, String message, String details) {
     this.errorCode = errorCode;
     this.message = message;
     this.details = details;
 }

 public String getErrorCode() {
     return errorCode;
 }

 public void setErrorCode(String errorCode) {
     this.errorCode = errorCode;
 }

 public String getMessage() {
     return message;
 }

 public void setMessage(String message) {
     this.message = message;
 }

 public String getDetails() {
     return details;
 }

 public void setDetails(String details) {
     this.details = details;
 }
}
