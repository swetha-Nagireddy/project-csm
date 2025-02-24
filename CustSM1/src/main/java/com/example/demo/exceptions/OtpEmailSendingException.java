package com.example.demo.exceptions;
 
public class OtpEmailSendingException extends RuntimeException {
 
    // Constructor with message
    public OtpEmailSendingException(String message) {
        super(message);
    }
}
 