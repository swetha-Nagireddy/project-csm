package com.example.demo.exceptions;

public class EmailNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailNotFoundException(String msg) {
        super(msg);
    }
}