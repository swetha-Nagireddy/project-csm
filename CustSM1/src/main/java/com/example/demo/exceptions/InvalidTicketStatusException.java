package com.example.demo.exceptions;
 
public class InvalidTicketStatusException extends RuntimeException {
    public InvalidTicketStatusException(String message) {
        super(message);
    }
}
 