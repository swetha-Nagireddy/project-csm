package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.CustomerAlreadyExistsException;
import com.example.demo.exceptions.CustomerNotFoundException;
import com.example.demo.exceptions.EmailNotFoundException;
import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepository;

import jakarta.transaction.Transactional;

/**
 * CustomerService Class
 * This Class contains all business logic implementations for Customer functionalities
 * 
 * @author Swetha.N
 */

@Service
@Transactional
public class CustomerService {
	
	private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bencoder;
    private final EmailService emailService;
    private  final OtpService otpService;

    public CustomerService(CustomerRepository customerRepository, BCryptPasswordEncoder bencoder,EmailService emailService,OtpService otpService) {
        this.customerRepository = customerRepository;
		this.bencoder = bencoder;
		this.emailService = emailService;
		this.otpService = otpService;
    }
    
    private static Logger logger = Logger.getLogger(CustomerService.class);

    /**
     * Retrieves a list of all customers.
     * 
     */
    public List<Customer> showCustomer() {
        logger.info("Fetching all customers");
        return customerRepository.findAll();
    }
    
    /**
     * Searches for a customer by their unique ID.
     * Throws a CustomerNotFoundException if no customer is found with the given ID.
     */
    public Customer searchById(int id) {
        logger.info("Searching customer by ID: " + id);
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        
        if (!optionalCustomer.isPresent()) {
            logger.error("Customer not found for ID: " + id);
            throw new CustomerNotFoundException("Customer not found for ID: " + id);
        }
        return optionalCustomer.get();
    }

    /**
     * Searches for a customer by their username.
     */
    
    public Customer searchByUsername(String userName) {
        logger.info("Searching customer by username: " + userName);
        return customerRepository.findByCustomerUsername(userName);
    }

    /**
     * Authenticates a customer based on their username and password.
     * Throws InvalidCredentialsException if the credentials are incorrect.
     */
    
    public Customer login(String customerUserName, String customerPassword) {
        logger.info("Attempting login for username: " + customerUserName);
        Customer customer = customerRepository.findByCustomerUsername(customerUserName);
        if (customer == null || !bencoder.matches(customerPassword, customer.getCustomerPassword())) {
            logger.warn("Invalid login attempt for username: " + customerUserName);
            throw new InvalidCredentialsException("Invalid username or password");
        }
        logger.info("Login successful for username: " + customerUserName);
        return customer;
    }

    /**
     * Registers a new customer. Checks if the email or phone number already exists.
     * If not, it encodes the password and saves the customer to the database.
     * Sends a registration confirmation email to the customer.
     */
    
    public String addCustomer(Customer customer) {
        logger.info("Adding new customer: " + customer.getCustomerEmail());
        
        // Check if email already exists
        if (customerRepository.findByCustomerEmail(customer.getCustomerEmail()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer with email " + customer.getCustomerEmail() + " already exists.");
        }
        if (customerRepository.findByCustomerPhno(customer.getCustomerPhno()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer with phone number " + customer.getCustomerPhno() + " already exists.");
        }
        
        customer.setCustomerPassword(bencoder.encode(customer.getCustomerPassword()));
        customerRepository.save(customer);
        logger.info("Customer added successfully: " + customer.getCustomerUsername());
        
        // Send a registration success email to the customer
        String emailBody = "Dear " + customer.getCustomerLastname() + ",\n\n" +
                           "Welcome to our platform! Your account has been successfully registered.\n\n" +
                           "Thank you for joining us!\n\n" +
                           "Best Regards,\n" +
                           "The Team";
        emailService.sendEmail(customer.getCustomerEmail(), "Registration Successful", emailBody);
        
        return "User Added Successfully";
    }

    /**
     * Updates an existing customer's information in the database.
     */
    
    public void updateCustomer(Customer customer) {
        logger.info("Updating customer: " + customer.getCustomerId());
        customerRepository.save(customer);
        logger.info("Customer updated successfully: " + customer.getCustomerId());
    }

    /**
     * Deletes a customer by their ID.
     */
    
    public void deleteCustomer(int customerId) {
        logger.info("Deleting customer: " + customerId);
        customerRepository.deleteById(customerId);
        logger.info("Customer deleted successfully: " + customerId);
    }

    /**
     * Initiates a password reset process by sending an OTP to the customer's email.
     * @throws EmailNotFoundException if the email does not exist in the database.
     */
    
    public String forgotPassword(String email) {
        logger.info("Processing forgot password request for email: " + email);
        Optional<Customer> user = customerRepository.findByCustomerEmail(email);
        if (user.isEmpty()) {
            logger.error("Email not found: " + email);
            throw new EmailNotFoundException("Email not found");
        }
        String otp = otpService.generateOtp(email);
        logger.info("Generated OTP for email: " + email);
        logger.info("Generated OTP: " + otp);
        
        // Send OTP to the email
        emailService.sendOtpEmail(email, otp);
 
        return "OTP sent to your email";
    }
    
    /**
     * Validates the OTP provided by the customer.
     */
    
    public boolean validateOtpLogic(String email, String otp) {
		logger.info("Validating OTP for email: " + email);
        return otpService.validateOtp(email, otp);
    }

    /**
     * Resets the password for a customer after verifying their email.
     * @throws EmailNotFoundException if the email does not exist in the database.
     */
    
    public String resetPassword(String email, String newPassword) {
        logger.info("Resetting password for email: " + email);
        Optional<Customer> user = customerRepository.findByCustomerEmail(email);
        if (user.isEmpty()) {
            logger.error("Email not found: " + email);
            throw new EmailNotFoundException("Email not found");
        }
        String hashedPassword = bencoder.encode(newPassword);
        Customer existingCustomer = user.get();
        existingCustomer.setCustomerPassword(hashedPassword);
        customerRepository.save(existingCustomer);
        logger.info("Password reset successful for email: " + email);
        return "Password updated successfully";
    }

    
	

    /**
     * Updates the password for an existing customer.
     */
    
    public boolean updateCustomerPassword(Customer existingCustomer, String newPassword) {
        try {
            logger.info("Updating password for customer ID: " + existingCustomer.getCustomerId());
            if (newPassword != null && !newPassword.isEmpty()) {
                // Hash the new password
                String hashedPassword = bencoder.encode(newPassword);
                existingCustomer.setCustomerPassword(hashedPassword);
                customerRepository.save(existingCustomer);  // Save updated customer in the repository
                return true;
            }
            logger.warn("Password update failed: new password is empty");
            return false;  // If password is null or empty, return false
        } catch (Exception e) {
            logger.info("Error updating password: " + e.getMessage());
            logger.error("Error updating password for customer ID: " + existingCustomer.getCustomerId() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves the total count of customers in the system.
     */
    
    public long getCustomerCount() {
        return customerRepository.countAllCustomers();
    }
}
