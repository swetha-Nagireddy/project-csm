package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.CustomerAlreadyExistsException;
import com.example.demo.exceptions.CustomerNotFoundException;
import com.example.demo.exceptions.EmailNotFoundException;
import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OtpService;



@RestController
@Configuration
@RequestMapping(value="/customer")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {
	
	private final OtpService otpService;
    private final CustomerService customerService;
    public CustomerController(OtpService otpService, CustomerService customerService) {
    	this.customerService = customerService;
    	this.otpService = otpService;
    }
    
    @GetMapping(value="/showCustomer")
    public List<Customer> showCustomer() {
        return customerService.showCustomer();
    }
 
    @GetMapping(value="/searchCustomer/{id}")
    public ResponseEntity<Customer> get(@PathVariable int id) {
        try {
            Customer customer = customerService.searchById(id);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value="/searchByCustomerUserName/{user}")
    public ResponseEntity<Customer> getByUser(@PathVariable String user) {
        try {
            Customer customer = customerService.searchByUsername(user);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch(NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping(value = "/customerLogin/{user}/{pwd}")
    public ResponseEntity<Object> login(@PathVariable String user, @PathVariable String pwd) {
        try {
            Customer customer = customerService.login(user, pwd);
            return ResponseEntity.ok(customer);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during login");
        }
    }
    
    @PostMapping(value = "/addCustomer")
    public ResponseEntity<String> addCustomer(@RequestBody Customer request) {
    	try {
            if (request.getCustomerUsername() == null || request.getCustomerEmail() == null ||
                request.getCustomerPassword() == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }
            
            String result = customerService.addCustomer(request);
            return ResponseEntity.ok(result);
            
        } catch (CustomerAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Internal server error");
        }
    }

    
    @PutMapping(value="/updateCustomer")
    public void updateCustomer(@RequestBody Customer customer) {
        customerService.updateCustomer(customer);
    }
    
    @DeleteMapping(value="/deleteCustomer/{id}")
    public void deleteCustomer(@PathVariable int id) {
        customerService.deleteCustomer(id);
    }
    
    @PostMapping("/forgotpassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            String response = customerService.forgotPassword(email);
            return ResponseEntity.ok(response);
        } catch (EmailNotFoundException  e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending OTP");
        }
    }
    
 // Reset password
    @PostMapping("/resetpassword")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            String response = customerService.resetPassword(email, newPassword);
            return ResponseEntity.ok(response);
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while resetting password");
        }
    }
 
		@PostMapping("/validateotp")
		    public ResponseEntity<String> validateOtp(@RequestParam String email, @RequestParam String otp) {
			
		        if (otpService.validateOtp(email, otp)) {
		            return ResponseEntity.ok("OTP is valid");
		        } else {
		            return ResponseEntity.badRequest().body("Invalid OTP");
		        }
		   }
 
 
	// Update customer password
	@PutMapping(value="/updateCustomerPassword/{customerUsername}")
	public ResponseEntity<String> updateCustomerPassword(@PathVariable String customerUsername, @RequestBody Customer customer) {
	    try {
	        // Search for the customer by username
	        Customer existingCustomer = customerService.searchByUsername(customerUsername);
	        
	        if (existingCustomer == null) {
	            // Customer not found, return NOT_FOUND
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
	        }
	
	        // If password update is required, call the service to update it
	        boolean updateSuccess = customerService.updateCustomerPassword(existingCustomer, customer.getCustomerPassword());
	        
	        if (updateSuccess) {
	            return ResponseEntity.ok("Customer password updated successfully!");
	        } else {
	            // If password was not updated (i.e., empty or null password), handle it here
	            return ResponseEntity.badRequest().body("Password update failed. Please provide a valid password.");
	        }
	
	    } catch (Exception e) {
	        // Handle unexpected errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
	    }
	}
	
	// Endpoint to get the total count of employees (including managers)
    @GetMapping("/customers/count")
    public long getCustomerCount() {
        return customerService.getCustomerCount();
    }
 
}

