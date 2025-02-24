package com.example.demo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Random;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.service.OtpService;

 class OtpServiceTest {

    @InjectMocks
    private OtpService otpService;  // The service being tested

    @Mock
    private Map<String, String> otpStorage;  // Mocking the internal OTP storage (HashMap)

    @BeforeEach
   void setUp() {
    	 MockitoAnnotations.openMocks(this);

    }

    @Test
    void testGenerateOtp() {
        // Arrange: Prepare the necessary inputs
        String email = "user@example.com";

        // Mock the random behavior if needed, since Random is used for generating OTP
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt(900000)).thenReturn(123456);  // Example of a fixed OTP number

        // Inject the mock random into the service using reflection or constructor injection
        otpService = new OtpService();
        
        // Act: Call the method under test
        String otp = otpService.generateOtp(email);

        // Assert: Verify that the OTP was generated correctly and stored
        assertNotNull(otp);
        assertEquals(6, otp.length()); // Length should be 6

        // Check if OTP is a number
        assertTrue(otp.matches("\\d{6}"));
    }

    @Test
     void testValidateOtp_ValidOtp() {
        // Arrange
        String email = "user@example.com";
        String correctOtp = "123456";

        // Prepare the OTP storage with a mock value
        otpService.generateOtp(email);  // Assume this will store the OTP
        
        // Act: Validate the OTP
        boolean isValid = otpService.validateOtp(email, correctOtp);

        // Assert: Check if the OTP validation is successful
        assertTrue(isValid);
    }

    @Test
     void testValidateOtp_InvalidOtp() {
        // Arrange
        String email = "user@example.com";
        String incorrectOtp = "654321";

        // Generate an OTP for the email
        otpService.generateOtp(email);

        // Act: Validate with the wrong OTP
        boolean isValid = otpService.validateOtp(email, incorrectOtp);

        // Assert: OTP validation should fail for incorrect OTP
        assertFalse(isValid);
    }

    @Test
    void testClearOtp() {
        // Arrange
        String email = "user@example.com";

        // Generate OTP and store it
        otpService.generateOtp(email);

        // Act: Clear the OTP for the email
        otpService.clearOtp(email);

        // Assert: Check that OTP was removed from the storage
        boolean isOtpRemoved = otpService.validateOtp(email, "123456");
        assertFalse(isOtpRemoved);  // Should be false since OTP was cleared
    }
}
