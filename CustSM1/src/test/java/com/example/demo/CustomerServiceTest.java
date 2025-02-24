package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.exceptions.CustomerAlreadyExistsException;
import com.example.demo.exceptions.CustomerNotFoundException;
import com.example.demo.exceptions.EmailNotFoundException;
import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepository;
import com.example.demo.service.CustomerService;
import com.example.demo.service.EmailService;
import com.example.demo.service.OtpService;
 
class CustomerServiceTest {
 
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private BCryptPasswordEncoder bencoder;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private OtpService otpService;
    
    @InjectMocks
    private CustomerService customerService;
    
    private Customer customer1;
    
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testShowCustomer() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer());
        
        when(customerRepository.findAll()).thenReturn(customers);
        
        List<Customer> result = customerService.showCustomer();
        assertEquals(1, result.size());
    }
    
    @Test
    void testSearchById_Success() {
        customer1 = new Customer(1, "John", "Doe", "123 Street, Area 1", "600001", "Male",
                "johndoe", "password123", "john.doe@example.com", "9876543210",
                13.0827, 80.2707, "Chennai", "Tamil Nadu");
        
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer1));
        Customer found = customerService.searchById(1);
        assertNotNull(found);
        assertEquals(1, found.getCustomerId());
    }

    @Test
    void testSearchById_CustomerNotFound() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.searchById(99));
    }
    
    @Test
    void testSearchByUsername() {
        Customer customer = new Customer();
        customer.setCustomerUsername("testUser");
        
        when(customerRepository.findByCustomerUsername("testUser")).thenReturn(customer);
        
        Customer result = customerService.searchByUsername("testUser");
        assertEquals("testUser", result.getCustomerUsername());
    }
    
    @Test
    void testLogin_Success() {
        Customer customer = new Customer();
        customer.setCustomerUsername("testUser");
        customer.setCustomerPassword("encodedPassword");
        
        when(customerRepository.findByCustomerUsername("testUser")).thenReturn(customer);
        when(bencoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        
        Customer result = customerService.login("testUser", "rawPassword");
        assertEquals("testUser", result.getCustomerUsername());
    }
    
    @Test
    void testLogin_InvalidCredentials() {
        when(customerRepository.findByCustomerUsername("invalidUser")).thenReturn(null);
        
        assertThrows(InvalidCredentialsException.class, () -> {
            customerService.login("invalidUser", "password");
        });
    }
    
    @Test
    void testAddCustomer_Success() {
        customer1 = new Customer(1, "John", "Doe", "123 Street, Area 1", "600001", "Male",
                "johndoe", "password123", "john.doe@example.com", "9876543210",
                13.0827, 80.2707, "Chennai", "Tamil Nadu");
        
        when(customerRepository.findByCustomerEmail(customer1.getCustomerEmail())).thenReturn(Optional.empty());
        when(bencoder.encode(customer1.getCustomerPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);
        
        String result = customerService.addCustomer(customer1);
        assertEquals("User Added Successfully", result);
        verify(emailService, times(1)).sendEmail(eq(customer1.getCustomerEmail()), eq("Registration Successful"), anyString());
    }

    @Test
    void testAddCustomer_CustomerAlreadyExists() {
        customer1 = new Customer(1, "John", "Doe", "123 Street, Area 1", "600001", "Male",
                "johndoe", "password123", "john.doe@example.com", "9876543210",
                13.0827, 80.2707, "Chennai", "Tamil Nadu");
        
        when(customerRepository.findByCustomerEmail(customer1.getCustomerEmail())).thenReturn(Optional.of(customer1));
        
        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.addCustomer(customer1));
    }
    
    @Test
    void testUpdateCustomer() {
        Customer customer = new Customer();
        customerService.updateCustomer(customer);
        verify(customerRepository).save(customer);
    }
    
    @Test
    void testDeleteCustomer() {
        customerService.deleteCustomer(1);
        verify(customerRepository).deleteById(1);
    }
    
    @Test
    void testForgotPassword_Success() {
        Customer customer = new Customer();
        customer.setCustomerEmail("test@example.com");
        
        when(customerRepository.findByCustomerEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(otpService.generateOtp("test@example.com")).thenReturn("123456");
        
        String result = customerService.forgotPassword("test@example.com");
        
        assertEquals("OTP sent to your email", result);
        verify(emailService).sendOtpEmail("test@example.com", "123456");
    }
    
    @Test
    void testForgotPassword_EmailNotFound() {
        when(customerRepository.findByCustomerEmail("notfound@example.com")).thenReturn(Optional.empty());
        
        assertThrows(EmailNotFoundException.class, () -> {
            customerService.forgotPassword("notfound@example.com");
        });
    }
    
    @Test
    void testResetPassword_Success() {
        Customer customer = new Customer();
        customer.setCustomerEmail("test@example.com");
        
        when(customerRepository.findByCustomerEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(bencoder.encode("newPassword")).thenReturn("encodedNewPassword");
        
        String result = customerService.resetPassword("test@example.com", "newPassword");
        
        assertEquals("Password updated successfully", result);
        verify(customerRepository).save(customer);
    }
    
    @Test
    void testResetPassword_EmailNotFound() {
        when(customerRepository.findByCustomerEmail("notfound@example.com")).thenReturn(Optional.empty());
        
        assertThrows(EmailNotFoundException.class, () -> {
            customerService.resetPassword("notfound@example.com", "newPassword");
        });
    }

    @Test
    void testUpdateCustomerPassword_Success() {
        customer1 = new Customer(1, "John", "Doe", "123 Street, Area 1", "600001", "Male",
                "johndoe", "password123", "john.doe@example.com", "9876543210",
                13.0827, 80.2707, "Chennai", "Tamil Nadu");
        
        when(bencoder.encode("newPassword")).thenReturn("hashedPassword");
        when(customerRepository.save(customer1)).thenReturn(customer1);
        
        boolean result = customerService.updateCustomerPassword(customer1, "newPassword");
        assertTrue(result);
    }

    @Test
    void testUpdateCustomerPassword_EmptyPassword() {
        customer1 = new Customer(1, "John", "Doe", "123 Street, Area 1", "600001", "Male",
                "johndoe", "password123", "john.doe@example.com", "9876543210",
                13.0827, 80.2707, "Chennai", "Tamil Nadu");
        
        boolean result = customerService.updateCustomerPassword(customer1, "");
        assertFalse(result);
    }

    @Test
    void testUpdateCustomerPassword_Exception() {
        customer1 = new Customer(1, "John", "Doe", "123 Street, Area 1", "600001", "Male",
                "johndoe", "password123", "john.doe@example.com", "9876543210",
                13.0827, 80.2707, "Chennai", "Tamil Nadu");
        
        when(bencoder.encode("newPassword")).thenThrow(new RuntimeException("Encoding error"));
        boolean result = customerService.updateCustomerPassword(customer1, "newPassword");
        assertFalse(result);
    }
    
    @Test
    void testGetCustomerCount_Success() {
        when(customerRepository.countAllCustomers()).thenReturn(2L);
        long count = customerService.getCustomerCount();
        assertEquals(2, count);
    }

    @Test
    void testGetCustomerCount_ZeroCustomers() {
        when(customerRepository.countAllCustomers()).thenReturn(0L);
        long count = customerService.getCustomerCount();
        assertEquals(0, count);
    }

    @Test
    void testGetCustomerCount_Exception() {
        when(customerRepository.countAllCustomers()).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> customerService.getCustomerCount());
    }
}
 
 