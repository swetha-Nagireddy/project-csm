package com.example.demo.model;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Employee Entity Class
 * Represents an employee in the system with attributes such as personal details, designation, department,
 * salary, and contact information. This class maps to the "Employee" table in the database.
 * 
 * @author Nitisha.S, Srihari.P, Manjunath.AS
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    /**
     * Unique identifier for each employee.
     * This field is automatically generated as a primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="EMPLOYEE_ID")
    private int employeeId;
    
    /**
     * First name of the employee.
     */
    @Column(name="EMPLOYEE_FIRSTNAME")
    private String employeeFirstName;
    
    /**
     * Last name of the employee.
     */
    @Column(name="EMPLOYEE_LASTNAME")
    private String employeeLastName;
    
    /**
     * Job designation or title of the employee.
     * Stores the employee's designation within the company (e.g., Representative/Employee Manager, Admin).
     */
    @Column(name="EMPLOYEE_DESIGNATION")
    private String employeeDesignation;
    
    /**
     * Department the employee belongs to.
     */
    @Column(name="EMPLOYEE_DEPT")
    private String employeeDept;
    
    /**
     * Gender of the employee.
     * Stores the gender of the employee (e.g., Male, Female, Other).
     */
    @Column(name="EMPLOYEE_GENDER")
    private String employeeGender;
    
    /**
     * Date of birth of the employee.
     */
    @Column(name="EMPLOYEE_DOB")
    private Date employeeDob;
    
    /**
     * Date of joining the employee to the company.
     */
    @Column(name="EMPLOYEE_DOJ")
    private Date employeeDoj;
    
    /**
     * Take-home salary of the employee.
     */
    @Column(name="EMPLOYEE_TAKEHOME")
    private double employeeTakeHome;
    
    /**
     * Email address of the employee.
     */
    @Column(name="EMPLOYEE_EMAIL")
    private String employeeEmail;
    
    /**
     * Phone number of the employee.
     */
    @Column(name="EMPLOYEE_PHNO")
    private String employeePhNo;
    
    /**
     * Password for the employee's account.
     * Stores the password used for authentication and login.
     */
    @Column(name="EMPLOYEE_PASSWORD")
    private String employeePassword;
    
    /**
     * ID of the employee's manager.
     * Stores the employee's manager's unique identifier (references another employee).
     */
    @Column(name="EMPLOYEE_MANAGER_ID")
    private Integer employeeManagerId; // Manager ID
}
