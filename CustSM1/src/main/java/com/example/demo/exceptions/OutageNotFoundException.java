package com.example.demo.exceptions;

public class OutageNotFoundException extends RuntimeException {

    // Constructor for custom message
    public OutageNotFoundException(String message) {
        super(message);
    }
}