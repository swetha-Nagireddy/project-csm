package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.repo.EmployeeRepository;
import com.example.demo.exceptions.EmailNotFoundException;
import com.example.demo.exceptions.EmployeeNotFoundException;
import com.example.demo.exceptions.InvalidRoleException;
import com.example.demo.model.Employee;

import jakarta.transaction.Transactional;

/**
 * EmployeeService Class
 * This Class contains all business logic implementations for Employee, Manager and Admin functionalities
 * 
 * @author Nitisha.S, Srihari.P, Manjunath.AS
 */

@Service
@Transactional
public class EmployeeService implements UserDetailsService{
	
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final OtpService otpService;
    private EmployeeRepository employeeRepository;
	 
    public EmployeeService(PasswordEncoder encoder,
	    		EmailService emailService, OtpService otpService, EmployeeRepository employeeRepository) {
        this.encoder = encoder;
		this.emailService = emailService;
		this.otpService = otpService;
		this.employeeRepository = employeeRepository;
    }
	
    private static Logger logger = Logger.getLogger(EmployeeService.class);

    /**
     * Fetches and returns a list of all employees from the repository.
     */
    
	public List<Employee> showEmployee() {
		logger.info("Fetching all employees");
		return employeeRepository.findAll();
	}
	
	/**
	 * Searches for an employee by their first name.
	 */
	
	public Employee searchByFirstName(String employeeFirstName) {
		logger.info("Searching for employee by first name: " + employeeFirstName);
		return employeeRepository.findByEmployeeFirstName(employeeFirstName);
	}

    /**
     * Searches for an employee by their ID.
     * @throws EmployeeNotFoundException if employee with the given ID is not found.
     */
	
	public Employee searchById(int id) {
		logger.info("Searching for employee by ID: " + id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return employee.get();
        } else {
        	logger.warn("Employee not found with ID: " + id);
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        }
    }
	
	/**
	 * Finds employees who report to a specific manager based on the manager's ID.
	 */
	
	public List<Employee> findByManagerId(int employeeManagerId) {
		logger.info("Finding employees by manager ID: " + employeeManagerId);
        return employeeRepository.findByEmployeeManagerId(employeeManagerId);
    }
	
	/**
	 * Searches for an employee by their email address.
	 * @throws EmailNotFoundException if the email is not found in the database.
	 */
	
	public Optional<Employee> searchByEmployeeEmail(String username) {
		logger.info("Searching for employee by email: " + username);
	    Optional<Employee> employee = employeeRepository.findByEmployeeEmail(username);
	    if (employee.isEmpty()) {
	    	logger.warn("Employee not found with email: " + username);
	        throw new EmailNotFoundException("Employee with email " + username + " not found.");
	    }
	    return employee;
	}
	
	/**
	 * Searches for an employee by their phone number.
	 */
	
	public Employee searchByEmployeePhNo(String employeePhNo) {
		logger.info("Searching for employee by phone number: " + employeePhNo);
		return employeeRepository.findByEmployeePhNo(employeePhNo);
	}
	
	/**
	 * Finds employees based on their designation.
	 */
	
	public List<Employee> findByDesignation(String employeeDesignation) {
		logger.info("Finding employees by designation: " + employeeDesignation);
	    return employeeRepository.findByEmployeeDesignation(employeeDesignation);
	}
	
	/**
	 * Adds a new employee to the system, encodes their password, and sends a welcome email with credentials.
	 */
	
	public String addEmployee (Employee employee) {
		logger.info("Adding new employee: " + employee.getEmployeeEmail());
		 // Store the original password before encoding
	    String originalPassword = employee.getEmployeePassword();
	    
		employee.setEmployeePassword(encoder.encode(originalPassword));
		employeeRepository.save(employee);

       String subject = "Welcome to the Company - Your Credentials";
       String body = "Dear " + employee.getEmployeeFirstName() + ",\n\n"
               + "Welcome to the company!\n\n"
               + "Your login credentials are:\n"
               + " - Email: " + employee.getEmployeeEmail() + "\n"
               + " - Password: " + originalPassword + "\n\n"
               + "Please change your password after logging in.\n\n"
               + "Best Regards,\nHR Team";

       emailService.sendEmail(employee.getEmployeeEmail(), subject, body);
		return "User Added Successfully";
	}
	
	/**
	 * Verifies employee login credentials using email and password.
	 * @throws InvalidRoleException if the credentials are invalid.
	 */
	
	public String login(String employeeEmail, String password) {
		logger.info("Employee login attempt: " + employeeEmail);
        long count = employeeRepository.countByEmployeeEmailAndEmployeePassword(employeeEmail, password);
        if (count == 0) {
        	logger.warn("Invalid login attempt for email: " + employeeEmail);
            throw new InvalidRoleException("Invalid login credentials or role.");
        }
        return "1";
    }
	
	/**
	 * Updates the details of an existing employee in the system.
	 */
	
	public void updateEmployee (Employee employee) {
		logger.info("Updating employee: " + employee.getEmployeeEmail());
		employeeRepository.save(employee);
	}
	
	/**
	 * Deletes an employee based on their employee ID.
	 */
	
	public void deleteEmployee (int empno) {
		logger.info("Deleting employee with ID: " + empno);
		employeeRepository.deleteById(empno);
	}

    /**
     * Loads user details by username (email), used for authentication.
     * @throws UsernameNotFoundException if the employee is not found.
     */
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Loading user by username: " + username);
		Optional<Employee> userDetail = employeeRepository.findByEmployeeEmail(username);
		return userDetail.map(UserDataDetails :: new)
				.orElseThrow( () -> new UsernameNotFoundException("User Not Found" +username));
	}
	
	/**
	 * Initiates the forgot password process by sending an OTP to the employee's email.
	 */
	
	public String forgotPasswordLogic(String email) {
		logger.info("Forgot password request for email: " + email);
        Optional<Employee> user = employeeRepository.findByEmployeeEmail(email);
        
        if (user.isEmpty()) {
        	logger.warn("Email not found in database: " + email);
        	logger.error("Email not found in the database: " + email);
            return "Email not found";
        }
 
        String otp = otpService.generateOtp(email);
        logger.info("Generated OTP: " + otp);
        emailService.sendOtpEmail(email, otp);
 
        return "OTP sent to your email";
    }
	
	/**
	 * Validates the OTP provided by the employee during the password reset process.
	 */
	
	public boolean validateOtpLogic(String email, String otp) {
		logger.info("Validating OTP for email: " + email);
        return otpService.validateOtp(email, otp);
    }
	
	/**
	 * Resets the employee's password after validating the OTP and setting the new password.
	 */
	
	public boolean resetPasswordLogic(String email, String newPassword) {
		logger.info("Resetting password for email: " + email);
        Employee employee = employeeRepository.findByEmployeeEmail(email).orElse(null);
        if (employee == null) {
        	logger.warn("Email not found: " + email);
            return false;
        }

        String hashedPassword = encoder.encode(newPassword);
        employee.setEmployeePassword(hashedPassword);
        employeeRepository.save(employee);
 
        // Clear the OTP (if applicable)
        otpService.clearOtp(email);
        return true; 
    }
	
	/**
	 * Updates the password for an existing employee.
	 */
	
	 public boolean updateEmployeePasswordLogic(String employeeEmail, String newPassword) {
		 	
		 	logger.info("Updating password for employee email: " + employeeEmail);
	        // Find the existing employee by their email
	        Employee existingEmployee = employeeRepository.findByEmployeeEmail(employeeEmail).orElse(null);
	        if (existingEmployee == null) {
	        	logger.warn("Employee not found: " + employeeEmail);
	            return false; // Employee not found
	        }
 
	        // Hash the new password
	        String hashedPassword = encoder.encode(newPassword);
	        existingEmployee.setEmployeePassword(hashedPassword); // Set the new password
 
	        // Update the employee details in the database
	        employeeRepository.save(existingEmployee);
	        return true; // Password successfully updated
	    }
	
	/**
	 * Gets the total count of employees (including managers) in the organization.
	 */
	 
	public long getEmployeeCount() {
        return employeeRepository.countAllEmployees();
    }

    /**
     * Gets the total count of managers in the organization.
     */
	
    public long getManagerCount() {
        return employeeRepository.countManagers();
    }

    /**
     * Gets the total count of admins in the organization.
     */
    
    public long getAdminCount() {
        return employeeRepository.countAdmins();
    }
    
    /**
     * Gets the number of employees under a specific manager by manager ID.
     */
    
    public long countEmployeesUnderManager(Integer managerId) {
        return employeeRepository.countEmployeesUnderManager(managerId);
    }
}
