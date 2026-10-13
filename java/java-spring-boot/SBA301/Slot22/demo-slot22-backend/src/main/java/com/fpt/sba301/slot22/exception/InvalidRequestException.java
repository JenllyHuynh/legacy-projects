package com.fpt.sba301.slot22.exception;

/** Input sai về nghiệp vụ (vd: parent không tồn tại) -> 400. */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) { super(message); }
}
