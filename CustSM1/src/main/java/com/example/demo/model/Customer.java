package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Customer Entity Class
 * Represents a customer in the system with various attributes such as personal details, contact information,
 * and geographical information. This class maps to the "Customer" table in the database.
 * 
 * @author Swetha.N
 */

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Customer")
public class Customer {
	
    /**
     * Unique identifier for each customer.
     * This field is automatically generated as a primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID")  
    private int customerId;
    
    /**
     * First name of the customer.
     * Stores the customer's first name.
     */
    @Column(name="CUSTOMER_FIRSTNAME")
    private String customerFirstname;
    
    /**
     * Last name of the customer.
     * Stores the customer's last name.
     */
    @Column(name="CUSTOMER_LASTNAME")
    private String customerLastname;
    
    /**
     * Address of the customer.
     * Stores the customer's residential address.
     */
    @Column(name="CUSTOMER_ADDRESS")
    private String customerAddress;
    
    /**
     * Pincode of the customer's address.
     * Stores the postal code for the customer's address.
     */
    @Column(name="CUSTOMER_PINCODE")
    private String customerPincode;
    
    /**
     * Gender of the customer.
     * Stores the gender of the customer (e.g., Male, Female, Other).
     */
    @Column(name="CUSTOMER_GENDER")
    private String customerGender;
    
    /**
     * Username for the customer.
     * Used for login or identification purposes in the system.
     */
    @Column(name="CUSTOMER_USERNAME")
    private String customerUsername;
    
    /**
     * Password for the customer.
     * Used for secure authentication of the customer.
     */
    @Column(name="CUSTOMER_PASSWORD")
    private String customerPassword;
    
    /**
     * Email address of the customer.
     * Stores the customer's email for communication and account management.
     */
    @Column(name="CUSTOMER_EMAIL")
    private String customerEmail;
    
    /**
     * Phone number of the customer.
     * Stores the customer's phone number for contact purposes.
     */
    @Column(name="CUSTOMER_PHNO")
    private String customerPhno;
    
    /**
     * Latitude of the customer's location.
     * Represents the geographical latitude for the customer's address.
     */
    @Column(name="CUSTOMER_LATITUDE")
    private double customerLatitude;
    
    /**
     * Longitude of the customer's location.
     * Represents the geographical longitude for the customer's address.
     */
    @Column(name="CUSTOMER_LONGITUDE") 
    private double customerLongitude;
    
    /**
     * City where the customer resides.
     * Stores the city name for the customer's location.
     */
    @Column(name="CUSTOMER_CITY") 
    private String customerCity;
    
    /**
     * State where the customer resides.
     * Stores the state name for the customer's location.
     */
    @Column(name="CUSTOMER_STATE") 
    private String customerState;
}
