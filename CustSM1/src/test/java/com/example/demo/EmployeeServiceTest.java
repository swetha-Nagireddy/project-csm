package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.eq;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.exceptions.EmailNotFoundException;
import com.example.demo.exceptions.EmployeeNotFoundException;
import com.example.demo.exceptions.InvalidRoleException;
import com.example.demo.model.Employee;
import com.example.demo.repo.EmployeeRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.OtpService;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordEncoder encoder;
    
    @Mock
    private BCryptPasswordEncoder bencoder;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee emp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowEmployee() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(emp));
        List<Employee> employees = employeeService.showEmployee();
        assertEquals(1, employees.size());
    }

    @Test
    void testSearchByFirstName_Success() {
        emp = new Employee(1, "Alice", "Johnson", "Manager", "HR", "Female", Date.valueOf("1990-05-15"), Date.valueOf("2020-06-10"), 75000.0, "alice@example.com", "9876543210", "securePass", null);
        when(employeeRepository.findByEmployeeFirstName("Alice")).thenReturn(emp);
        Employee found = employeeService.searchByFirstName("Alice");
        assertNotNull(found);
        assertEquals("Alice", found.getEmployeeFirstName());
    }

    @Test
    void testSearchByFirstName_EmployeeNotFound() {
        when(employeeRepository.findByEmployeeFirstName("Bob")).thenReturn(null);
        Employee found = employeeService.searchByFirstName("Bob");
        assertNull(found);
    }

    @Test
    void testSearchById_Success() {
        emp = new Employee(1, "Alice", "Johnson", "Manager", "HR", "Female",
                Date.valueOf("1990-05-15"), Date.valueOf("2020-06-10"), 75000.0,
                "alice@example.com", "9876543210", "securePass", null);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(emp));

        Employee found = employeeService.searchById(1);

        assertNotNull(found);
        assertEquals(1, found.getEmployeeId());
        assertEquals("Alice", found.getEmployeeFirstName());
    }

    @Test
    void testSearchById_EmployeeNotFound() {
        when(employeeRepository.findById(2)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.searchById(2);
        });

        assertEquals("Employee not found with ID: 2", exception.getMessage());
    }


    @Test
    void testSearchByIdNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.searchById(1));
    }

    @Test
    void testFindByManagerId() {
        when(employeeRepository.findByEmployeeManagerId(1)).thenReturn(Arrays.asList(emp));
        List<Employee> employees = employeeService.findByManagerId(1);
        assertEquals(1, employees.size());
    }

    @Test
    void testLoginSuccess() {
        when(employeeRepository.countByEmployeeEmailAndEmployeePassword("john@example.com", "password123"))
                .thenReturn(1L);
        assertEquals("1", employeeService.login("john@example.com", "password123"));
    }

    @Test
    void testLoginFailure() {
        when(employeeRepository.countByEmployeeEmailAndEmployeePassword("john@example.com", "wrongpass"))
                .thenReturn(0L);
        assertThrows(InvalidRoleException.class, () -> employeeService.login("john@example.com", "wrongpass"));
    }

    @Test
    void testAddEmployee_Success1() {
        emp = new Employee(1, "Alice", "Johnson", "Manager", "HR", "Female",
                Date.valueOf("1990-05-15"), Date.valueOf("2020-06-10"), 75000.0,
                "alice@example.com", "9876543210", "securePass", null);

        when(bencoder.encode("securePass")).thenReturn("encodedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(emp);

        String result = employeeService.addEmployee(emp);

        assertEquals("User Added Successfully", result);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(emailService, times(1)).sendEmail(eq("alice@example.com"), anyString(), anyString());
    }

    @Test
    void testAddEmployee_NullEmployee_ShouldThrowException1() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(null);
        });

        assertEquals("Employee object cannot be null", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_Success() {
        emp = new Employee(1, "Alice", "Johnson", "Manager", "HR", "Female",
                Date.valueOf("1990-05-15"), Date.valueOf("2020-06-10"), 75000.0,
                "alice@example.com", "9876543210", "securePass", null);

        when(employeeRepository.save(any(Employee.class))).thenReturn(emp);

        assertDoesNotThrow(() -> employeeService.updateEmployee(emp));

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }


    @Test
    void testDeleteEmployee() {
        doNothing().when(employeeRepository).deleteById(1);
        employeeService.deleteEmployee(1);
        verify(employeeRepository).deleteById(1);
    }

    @Test
    void testForgotPassword_Success() {
        emp = new Employee(1, "Alice", "Johnson", "Manager", "HR", "Female",
                Date.valueOf("1990-05-15"), Date.valueOf("2020-06-10"), 75000.0,
                "alice@example.com", "9876543210", "securePass", null);

        when(employeeRepository.findByEmployeeEmail("alice@example.com")).thenReturn(Optional.of(emp));
        when(otpService.generateOtp("alice@example.com")).thenReturn("123456");

        String response = employeeService.forgotPasswordLogic("alice@example.com");

        assertEquals("OTP sent to your email", response);
    }

    @Test
    void testForgotPassword_EmailNotFound() {
        when(employeeRepository.findByEmployeeEmail("bob@example.com")).thenReturn(Optional.empty());

        String response = employeeService.forgotPasswordLogic("bob@example.com");

        assertEquals("Email not found", response);
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    @Test
    void testValidateOtp_Success() {
        when(otpService.validateOtp("alice@example.com", "123456")).thenReturn(true);

        boolean result = employeeService.validateOtpLogic("alice@example.com", "123456");

        assertTrue(result);
        verify(otpService, times(1)).validateOtp("alice@example.com", "123456");
    }

    @Test
    void testValidateOtp_Failure_InvalidOtp() {
        when(otpService.validateOtp("alice@example.com", "654321")).thenReturn(false);

        boolean result = employeeService.validateOtpLogic("alice@example.com", "654321");

        assertFalse(result);
        verify(otpService, times(1)).validateOtp("alice@example.com", "654321");
    }

    @Test
    void testResetPassword_Success() {
        Employee employee = new Employee();
        employee.setEmployeeEmail("alice@example.com");
        employee.setEmployeePassword("oldPassword");

        when(employeeRepository.findByEmployeeEmail("alice@example.com")).thenReturn(Optional.of(employee));
        when(bencoder.encode("newPassword")).thenReturn("hashedNewPassword");

        boolean result = employeeService.resetPasswordLogic("alice@example.com", "newPassword");

        assertTrue(result);
        assertEquals("hashedNewPassword", employee.getEmployeePassword());
        verify(employeeRepository, times(1)).save(employee);
        verify(otpService, times(1)).clearOtp("alice@example.com");
    }

    @Test
    void testResetPassword_EmailNotFound() {
        when(employeeRepository.findByEmployeeEmail("unknown@example.com")).thenReturn(Optional.empty());

        boolean result = employeeService.resetPasswordLogic("unknown@example.com", "newPassword");

        assertFalse(result);
        verify(employeeRepository, never()).save(any());
        verify(otpService, never()).clearOtp(anyString());
    }

    
    @Test
    void testGetEmployeeCount() {
        when(employeeRepository.countAllEmployees()).thenReturn(10L);
        assertEquals(10L, employeeService.getEmployeeCount());
    }

    @Test
    void testGetManagerCount() {
        when(employeeRepository.countManagers()).thenReturn(5L);
        assertEquals(5L, employeeService.getManagerCount());
    }

    @Test
    void testGetAdminCount() {
        when(employeeRepository.countAdmins()).thenReturn(2L);
        assertEquals(2L, employeeService.getAdminCount());
    }
    
    @Test
    void testUpdateEmployeePassword_Success() {
        Employee employee = new Employee();
        employee.setEmployeeEmail("alice@example.com");
        employee.setEmployeePassword("oldPassword");

        when(employeeRepository.findByEmployeeEmail("alice@example.com")).thenReturn(Optional.of(employee));
        when(bencoder.encode("newPassword")).thenReturn("hashedNewPassword");

        boolean result = employeeService.updateEmployeePasswordLogic("alice@example.com", "newPassword");

        assertTrue(result);
        assertEquals("hashedNewPassword", employee.getEmployeePassword());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testUpdateEmployeePassword_EmployeeNotFound() {
        when(employeeRepository.findByEmployeeEmail("unknown@example.com")).thenReturn(Optional.empty());

        boolean result = employeeService.updateEmployeePasswordLogic("unknown@example.com", "newPassword");

        assertFalse(result);
        verify(employeeRepository, never()).save(any());
    }

    
    @Test
    void testSearchByEmployeeEmail_NotFound() {
        // Mocking repository to return empty
        when(employeeRepository.findByEmployeeEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Assert exception is thrown
        EmailNotFoundException exception = assertThrows(EmailNotFoundException.class, 
            () -> employeeService.searchByEmployeeEmail("unknown@example.com"));

        // Assertions
        assertEquals("Employee with email unknown@example.com not found.", exception.getMessage());
    }

    /**Null Email (Edge Case) **/
    @Test
    void testSearchByEmployeeEmail_NullEmail() {
        // Assert exception for null email
        assertThrows(IllegalArgumentException.class, 
            () -> employeeService.searchByEmployeeEmail(null));
    }

    /**Empty Email String (Edge Case) **/
    @Test
    void testSearchByEmployeeEmail_EmptyString() {
        // Mocking repository to return empty
        when(employeeRepository.findByEmployeeEmail("")).thenReturn(Optional.empty());

        // Assert exception is thrown
        EmailNotFoundException exception = assertThrows(EmailNotFoundException.class, 
            () -> employeeService.searchByEmployeeEmail(""));

        // Assertions
        assertEquals("Employee with email  not found.", exception.getMessage());
    }

    /**Test Case 5: Email with Only Spaces (Edge Case) **/
    @Test
    void testSearchByEmployeeEmail_OnlySpaces() {
        // Mocking repository to return empty
        when(employeeRepository.findByEmployeeEmail("   ")).thenReturn(Optional.empty());

        // Assert exception is thrown
        EmailNotFoundException exception = assertThrows(EmailNotFoundException.class, 
            () -> employeeService.searchByEmployeeEmail("   "));

        // Assertions
        assertEquals("Employee with email    not found.", exception.getMessage());

    }

    @Test
    void testSearchByEmployeePhNo_Found() {
        // Mock repository behavior
        when(employeeRepository.findByEmployeePhNo("9876543210")).thenReturn(emp);

        // Invoke method
        Employee result = employeeService.searchByEmployeePhNo("9876543210");

        // Assertions
        assertNotNull(result);
        assertEquals("9876543210", result.getEmployeePhNo());
        
    }
    /** Test Case 2: Employee Does Not Exist **/
    @Test
    void testSearchByEmployeePhNo_NotFound() {
        // Mock repository returning null
        when(employeeRepository.findByEmployeePhNo("1234567890")).thenReturn(null);

        // Invoke method
        Employee result = employeeService.searchByEmployeePhNo("1234567890");

        // Assertions
        assertNull(result);
    }

    /**Null Phone Number **/
    @Test
    void testSearchByEmployeePhNo_NullPhoneNumber() {
        // Assert exception for null input
        assertThrows(IllegalArgumentException.class, 
            () -> employeeService.searchByEmployeePhNo(null));
    }
    
    @Test
    void testFindByDesignation_Success() {
        when(employeeRepository.findByEmployeeDesignation("Manager")).thenReturn(Arrays.asList(emp));

        List<Employee> result = employeeService.findByDesignation("Manager");

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("Manager", result.get(0).getEmployeeDesignation());
        assertEquals("Manager", result.get(1).getEmployeeDesignation());

    }

    @Test
    void testFindByDesignation_NullDesignation() {
        when(employeeRepository.findByEmployeeDesignation(null)).thenReturn(Collections.emptyList());
        List<Employee> result = employeeService.findByDesignation(null);
        assertTrue(result.isEmpty());
    }


    @Test
    void testFindByDesignation_UnknownDesignation() {
        when(employeeRepository.findByEmployeeDesignation("Astronaut")).thenReturn(Collections.emptyList());
        List<Employee> result = employeeService.findByDesignation("Astronaut");
        assertTrue(result.isEmpty());
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Mock employee data
        Employee mockEmployee = new Employee();
        mockEmployee.setEmployeeEmail("test@example.com");
        mockEmployee.setEmployeePassword("password123");
        mockEmployee.setEmployeeDesignation("ROLE_USER");

        // Mock repository response
        when(employeeRepository.findByEmployeeEmail("test@example.com"))
            .thenReturn(Optional.of(mockEmployee));

        // Call the method under test
        UserDetails userDetails = employeeService.loadUserByUsername("test@example.com");

        // Assertions
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));

        // Verify repository call
        verify(employeeRepository, times(1)).findByEmployeeEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound_ShouldThrowException() {
        // Mock repository returning empty
        when(employeeRepository.findByEmployeeEmail("unknown@example.com"))
            .thenReturn(Optional.empty());

        // Assert that exception is thrown
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> employeeService.loadUserByUsername("unknown@example.com"));

        // Assertions
        assertEquals("User Not Foundunknown@example.com", exception.getMessage());

        // Verify repository call
        verify(employeeRepository, times(1)).findByEmployeeEmail("unknown@example.com");
    }
    
    @Test
     void testCountEmployeesUnderManager() {
        // Arrange: Prepare the mock and expected behavior
        Integer managerId = 1;
        long expectedCount = 5;

        // Mock the behavior of the repository method
        when(employeeRepository.countEmployeesUnderManager(managerId)).thenReturn(expectedCount);

        // Act: Call the method being tested
        long result = employeeService.countEmployeesUnderManager(managerId);

        // Assert: Verify the result
        assertEquals(expectedCount, result);

        // Verify the interaction with the repository
        verify(employeeRepository).countEmployeesUnderManager(managerId);
    }

}
