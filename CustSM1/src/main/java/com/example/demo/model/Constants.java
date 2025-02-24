package com.example.demo.model;

/**
 * Constants Class
 * This is a utility class that holds constant values used throughout the application. 
 * It prevents the instantiation of the class and ensures that these constants are used consistently.
 * The constants in this class are primarily used to define ticket statuses.
 * 
 * @author Manoj.KS
 */
public final class Constants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static final String OPEN = "OPEN";
    public static final String PENDING = "PENDING";
    public static final String CLOSED = "CLOSED";
}
