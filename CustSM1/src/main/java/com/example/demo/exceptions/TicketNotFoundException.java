package com.example.demo.exceptions;

public class TicketNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TicketNotFoundException(String msg) {
        super(msg);
    }
}
