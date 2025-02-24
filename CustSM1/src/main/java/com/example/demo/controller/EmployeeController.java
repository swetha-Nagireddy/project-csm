package com.example.demo.controller;
 
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import com.example.demo.config.JwtService;
import com.example.demo.exceptions.EmailNotFoundException;
import com.example.demo.exceptions.EmployeeNotFoundException;
import com.example.demo.exceptions.InvalidRoleException;
import com.example.demo.model.AuthRequest;
import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.UserDataDetails;
 
 
 
@RestController
@RequestMapping(value="/employee")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {
	
	private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    private String errorMessage = "An error occurred: ";

    public EmployeeController(EmployeeService employeeService, JwtService jwtService, 
    		AuthenticationManager authenticationManager) {
    	this.authenticationManager = authenticationManager;
    	this.employeeService = employeeService;
    	this.jwtService = jwtService;
    }
    
	@GetMapping(value="/showEmployee")
	public List<Employee> showEmployee(){
		return employeeService.showEmployee();
	}
	
	@GetMapping(value="/searchEmployee/{id}")
	public ResponseEntity<Employee> get(@PathVariable int id){
		try {
	        // Searching for the employee by ID
	        Employee employee = employeeService.searchById(id);
	        return new ResponseEntity<>(employee, HttpStatus.OK);
	    } catch (EmployeeNotFoundException e) {
	        // Catching the custom exception and returning a 404 NOT FOUND response
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        // Catching any other exceptions and returning a 500 INTERNAL SERVER ERROR response
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("/searchEmployeeEmail/{username}")
	public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String username) {
	    try {
	        // Fetch employee using service
	        Optional<Employee> employee = employeeService.searchByEmployeeEmail(username);
	        
	        // Check if employee is present
	        if (employee.isPresent()) {
	            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Handle case when employee is not found
	        }
	        
	    } catch (EmailNotFoundException e) {
	        // Catch any specific exceptions and return an appropriate status
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        // Catch other exceptions and return internal server error
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("/searchByManagerId/{employeeManagerId}")
	public ResponseEntity<Object> getEmployeesByManagerId(@PathVariable int employeeManagerId) {
	    List<Employee> employees = employeeService.findByManagerId(employeeManagerId);
	    if (employees.isEmpty()) {
	        return new ResponseEntity<>("No employees found under this manager.", HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<>(employees, HttpStatus.OK);
	}
 
	
	@GetMapping(value="/searchByEmployeeFirstName/{employeeFirstName}")
	public ResponseEntity<Employee> getName(@PathVariable String employeeFirstName){
		try {
			Employee employee = employeeService.searchByFirstName(employeeFirstName);
			return new ResponseEntity<>(employee,HttpStatus.OK);
			
		}catch(NoSuchElementException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(value="/searchByEmployeePhNo/{employeePhNo}")
	public ResponseEntity<Employee> getNo(@PathVariable String employeePhNo){
		try {
			Employee employee = employeeService.searchByEmployeePhNo(employeePhNo);
			return new ResponseEntity<>(employee,HttpStatus.OK);
			
		}catch(NoSuchElementException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(value="/searchByEmployeeDesignation/{employeeDesignation}")
	public ResponseEntity<List<Employee>> getEmployeesByDesignation(@PathVariable String employeeDesignation) {
	    List<Employee> employees = employeeService.findByDesignation(employeeDesignation);
	    if (employees.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<>(employees, HttpStatus.OK);
	}
	
	@PostMapping(value="/addEmployee")
	public void addEmployee(@RequestBody Employee employee) {
		employeeService.addEmployee(employee);
	}
	
	@GetMapping(value = "/employeeLogin/{employeeEmail}/{pwd}")
	public ResponseEntity<String> login(@PathVariable String employeeEmail, @PathVariable String pwd) {
	    try {
	        return ResponseEntity.ok(employeeService.login(employeeEmail, pwd));
	    } catch (InvalidRoleException ex) {
	        // You don't need to handle this here if you already have a global exception handler.
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
	    }
	}
	
	@PutMapping(value="/updateEmployee")
	public  ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee){
		try {
			employeeService.searchById(employee.getEmployeeId());
			employeeService.updateEmployee(employee);
			return new ResponseEntity<>(HttpStatus.OK);
			
		}catch(NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping(value="/deleteEmployee/{id}")
	public  ResponseEntity<Employee> deleteEmployee(@PathVariable int id){
		try {
			employeeService.searchById(id);
			employeeService.deleteEmployee(id);
			return new ResponseEntity<>(HttpStatus.OK);
			
		}catch(NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<String> adminProfile() {
        try {
            // Some business logic here (e.g., fetch user data)
            return new ResponseEntity<>("Welcome to Admin Profile", HttpStatus.OK);
        } catch (Exception e) {
            // Handle any exception that might occur during the method execution
            return new ResponseEntity<>("Error occurred while processing the Admin profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/manager/managerProfile")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<String> managerProfile() {
        try {
            // Some business logic here (e.g., fetch user data)
            return new ResponseEntity<>("Welcome to Manager Profile", HttpStatus.OK);
        } catch (Exception e) {
            // Handle any exception that might occur during the method execution
            return new ResponseEntity<>("Error occurred while processing the Manager profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/employee/employeeProfile")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<String> employeeProfile() {
        try {
            // Some business logic here (e.g., fetch user data)
            return new ResponseEntity<>("Welcome to Employee Profile", HttpStatus.OK);
        } catch (Exception e) {
            // Handle any exception that might occur during the method execution
            return new ResponseEntity<>("Error occurred while processing the Employee profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	 @PostMapping("/generateToken")
	    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		
		 UserDataDetails userDetails = (UserDataDetails) employeeService.loadUserByUsername(authRequest.getUsername());
		
	        // Check if the provided role matches the stored role
	        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority(authRequest.getDesignation()))) {
	            throw new BadCredentialsException("Invalid role provided!");
	        }
		
	        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
	        if (authentication.isAuthenticated()) {
	            return jwtService.generateToken(authRequest.getUsername());
	        } else {
	            throw new UsernameNotFoundException("invalid user request !");
	        }
	  }
	  
	 @PostMapping("/forgotpassword")
	 public ResponseEntity<String> forgotPassword(@RequestParam String email) {
		 try {
		        String response = employeeService.forgotPasswordLogic(email);
		        
		        if ("Email not found".equals(response)) {
		            // Return 400 Bad Request if email was not found
		            return ResponseEntity.badRequest().body(response);
		        }
		        
		        // Return 200 OK if OTP is successfully sent
		        return ResponseEntity.ok(response);
		    } catch (Exception e) {
		        // Catch any other exception and return 500 Internal Server Error
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( errorMessage + e.getMessage());
		    }
	 }
	   	 
	 @PostMapping("/validateotp")
	    public ResponseEntity<String> validateOtp(@RequestParam String email, @RequestParam String otp) {
	        try {
	            // Call the service method to validate the OTP
	            boolean isValid = employeeService.validateOtpLogic(email, otp);

	            if (isValid) {
	                return ResponseEntity.ok("OTP is valid");
	            } else {
	                return ResponseEntity.badRequest().body("Invalid OTP");
	            }

	        } catch (Exception e) {
	            // Catch any unexpected errors and return an internal server error response
	            return ResponseEntity.status(500).body(errorMessage + e.getMessage());
	        }
	    }
	
	 @PostMapping("/resetpassword")
	    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
	        try {
	            // Call the service method to reset the password
	            boolean isSuccess = employeeService.resetPasswordLogic(email, newPassword);

	            if (isSuccess) {
	                return ResponseEntity.ok("Password updated successfully");
	            } else {
	                return ResponseEntity.badRequest().body("Email not found");
	            }

	        } catch (Exception e) {
	            // Catch any unexpected errors and return an internal server error response
	            return ResponseEntity.status(500).body(errorMessage + e.getMessage());
	        }
	    }
	
	 @PutMapping(value = "/updateEmployeePassword/{employeeEmail}")
	    public ResponseEntity<String> updateEmployeePassword(@PathVariable String employeeEmail, @RequestBody Employee employee) {
	        try {
	            // Call the service method to update the password
	            boolean isSuccess = employeeService.updateEmployeePasswordLogic(employeeEmail, employee.getEmployeePassword());

	            if (isSuccess) {
	                return ResponseEntity.ok("Employee Password updated successfully!");
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
	            }
	        } catch (Exception e) {
	            // Catch any unexpected errors and return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + e.getMessage());
	        }
	    }
	 
	// Endpoint to get the total count of employees (including managers)
     @GetMapping("/employees/count")
     public long getEmployeeCount() {
         return employeeService.getEmployeeCount();
     }

     // Endpoint to get the count of managers
     @GetMapping("/managers/count")
     public long getManagerCount() {
         return employeeService.getManagerCount();
     }

     // Endpoint to get the count of admins
     @GetMapping("/admins/count")
     public long getAdminCount() {
         return employeeService.getAdminCount();
     }
     
  // Endpoint to get the count of employees under a specific manager
     @GetMapping("/employeescountundermanager/{managerId}")
     public ResponseEntity<Long> getEmployeeCountUnderManager(@PathVariable Integer managerId) {
         long employeeCount = employeeService.countEmployeesUnderManager(managerId);
         return ResponseEntity.ok(employeeCount); // Returning the count in the response
     }

}