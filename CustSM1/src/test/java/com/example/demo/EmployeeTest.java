package com.example.demo;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
 
import java.sql.Date;
 
import org.junit.jupiter.api.Test;
 
import com.example.demo.model.Employee;
 
class EmployeeTest {
 
    @Test
    void testEmployeeConstructorAndGetters() {
        // Given
        int employeeId = 1;
        String employeeFirstName = "John";
        String employeeLastName = "Doe";
        String employeeDesignation = "Software Engineer";
        String employeeDept = "IT";
        String employeeGender = "Male";
        Date employeeDob = Date.valueOf("1990-01-01");
        Date employeeDoj = Date.valueOf("2020-01-01");
        double employeeTakeHome = 5000.00;
        String employeeEmail = "john.doe@example.com";
        String employeePhNo = "9876543210";
        String employeePassword = "password123";
        Integer employeeManagerId = 10; // Optional manager ID
 
        // When
        Employee employee = new Employee(employeeId, employeeFirstName, employeeLastName, employeeDesignation,
                employeeDept, employeeGender, employeeDob, employeeDoj, employeeTakeHome,
                employeeEmail, employeePhNo, employeePassword, employeeManagerId);
 
        // Then
        assertEquals(employeeId, employee.getEmployeeId());
        assertEquals(employeeFirstName, employee.getEmployeeFirstName());
        assertEquals(employeeLastName, employee.getEmployeeLastName());
        assertEquals(employeeDesignation, employee.getEmployeeDesignation());
        assertEquals(employeeDept, employee.getEmployeeDept());
        assertEquals(employeeGender, employee.getEmployeeGender());
        assertEquals(employeeDob, employee.getEmployeeDob());
        assertEquals(employeeDoj, employee.getEmployeeDoj());
        assertEquals(employeeTakeHome, employee.getEmployeeTakeHome());
        assertEquals(employeeEmail, employee.getEmployeeEmail());
        assertEquals(employeePhNo, employee.getEmployeePhNo());
        assertEquals(employeePassword, employee.getEmployeePassword());
        assertEquals(employeeManagerId, employee.getEmployeeManagerId());
    }
 
    @Test
    void testSetters() {
        // Given
        Employee employee = new Employee();
 
        // When
        employee.setEmployeeId(1);
        employee.setEmployeeFirstName("John");
        employee.setEmployeeLastName("Doe");
        employee.setEmployeeDesignation("Software Engineer");
        employee.setEmployeeDept("IT");
        employee.setEmployeeGender("Male");
        employee.setEmployeeDob(Date.valueOf("1990-01-01"));
        employee.setEmployeeDoj(Date.valueOf("2020-01-01"));
        employee.setEmployeeTakeHome(5000.00);
        employee.setEmployeeEmail("john.doe@example.com");
        employee.setEmployeePhNo("9876543210");
        employee.setEmployeePassword("password123");
        employee.setEmployeeManagerId(10);
 
        // Then
        assertEquals(1, employee.getEmployeeId());
        assertEquals("John", employee.getEmployeeFirstName());
        assertEquals("Doe", employee.getEmployeeLastName());
        assertEquals("Software Engineer", employee.getEmployeeDesignation());
        assertEquals("IT", employee.getEmployeeDept());
        assertEquals("Male", employee.getEmployeeGender());
        assertEquals(Date.valueOf("1990-01-01"), employee.getEmployeeDob());
        assertEquals(Date.valueOf("2020-01-01"), employee.getEmployeeDoj());
        assertEquals(5000.00, employee.getEmployeeTakeHome());
        assertEquals("john.doe@example.com", employee.getEmployeeEmail());
        assertEquals("9876543210", employee.getEmployeePhNo());
        assertEquals("password123", employee.getEmployeePassword());
        assertEquals(10, employee.getEmployeeManagerId());
    }
 
    @Test
    void testDefaultConstructor() {
        // Given
        Employee employee = new Employee();
 
        // Then
        assertNotNull(employee);
        assertEquals(0, employee.getEmployeeId()); // Default value for int
        assertNull(employee.getEmployeeFirstName());
        assertNull(employee.getEmployeeLastName());
        assertNull(employee.getEmployeeDesignation());
        assertNull(employee.getEmployeeDept());
        assertNull(employee.getEmployeeGender());
        assertNull(employee.getEmployeeDob());
        assertNull(employee.getEmployeeDoj());
        assertEquals(0.0, employee.getEmployeeTakeHome());
        assertNull(employee.getEmployeeEmail());
        assertNull(employee.getEmployeePhNo());
        assertNull(employee.getEmployeePassword());
        assertNull(employee.getEmployeeManagerId());
    }
}
 
 