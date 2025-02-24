package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Customer;

/**
 * CustomerRepository Interface
 * This interface is a repository for managing Customer entities.
 * It extends JpaRepository which provides CRUD operations and additional query methods for Customer.
 * 
 * @author Swetha.N
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Customer findByCustomerUsername(String customerUserName);
    
    long countByCustomerUsernameAndCustomerPassword(String customerUserName, String customerPassword);
    
    Optional<Customer> findByCustomerPhno(String customerPhno);
    
    Optional<Customer> findByCustomerEmail(String customerEmail);
    
    boolean existsByCustomerEmail(String customerEmail);
    
    boolean existsByCustomerUsername(String customerUsername);
    
    @Query("SELECT COUNT(c) FROM Customer c ")
    long countAllCustomers();
}
