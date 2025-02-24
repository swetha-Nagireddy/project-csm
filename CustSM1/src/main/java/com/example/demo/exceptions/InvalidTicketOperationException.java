package com.example.demo.exceptions;

public class InvalidTicketOperationException extends RuntimeException {
    public InvalidTicketOperationException(String message) {
        super(message);
    }
}