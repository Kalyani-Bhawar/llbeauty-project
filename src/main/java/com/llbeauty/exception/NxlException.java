package com.llbeauty.exception;
public class NxlException extends RuntimeException {
    private final String errorCode; private final int statusCode;
    public NxlException(String msg, String errorCode, int statusCode){
        super(msg); this.errorCode=errorCode; this.statusCode=statusCode;
    }
    public String getErrorCode(){return errorCode;}
    public int getStatusCode(){return statusCode;}
}