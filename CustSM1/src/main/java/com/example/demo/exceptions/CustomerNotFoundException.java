package com.example.demo.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(String msg) {
        super(msg);
    }
}
