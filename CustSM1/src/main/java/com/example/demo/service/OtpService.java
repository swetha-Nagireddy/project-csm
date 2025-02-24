package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;

/**
 * OtpService Class
 * This Class contains all business logic implementations Sending Otp s and clearing them
 * 
 * @author Swetha.N
 */

@Service
public class OtpService {
    
    // Stores OTPs mapped to email addresses
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Random random = new Random();
 
    /**
     * Generates a 6-digit OTP for the given email and stores it in memory.
     */
    
    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + random.nextInt(900000)); // 6-digit OTP
        otpStorage.put(email, otp);
        return otp;
    }
    
    /**
     * Removes the OTP associated with the given email from storage.
     */
    
    public void clearOtp(String email) {
        otpStorage.remove(email);
    }
 
    /**
     * Validates the entered OTP for a given email.
     */
    
    public boolean validateOtp(String email, String enteredOtp) {
        return otpStorage.containsKey(email) && otpStorage.get(email).equals(enteredOtp);
    }
}
