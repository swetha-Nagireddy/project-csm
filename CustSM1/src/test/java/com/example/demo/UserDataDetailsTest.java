package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.demo.model.Employee;
import com.example.demo.service.UserDataDetails;

class UserDataDetailsTest {

    private Employee employee;
    private UserDataDetails userDataDetails;

    @BeforeEach
    void setUp() {
        // Mock Employee Data
        employee = new Employee();
        employee.setEmployeeEmail("test@example.com");
        employee.setEmployeePassword("securePassword");
        employee.setEmployeeDesignation("ROLE_ADMIN,ROLE_USER");

        // Create UserDataDetails instance
        userDataDetails = new UserDataDetails(employee);
    }

    @Test
    void testGetUsername() {
        assertEquals("test@example.com", userDataDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("securePassword", userDataDetails.getPassword());
    }

    @Test
    void testGetAuthorities() {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDataDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(userDataDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(userDataDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userDataDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(userDataDetails.isEnabled());
    }

    @Test
    void testSingleRole() {
        employee.setEmployeeDesignation("ROLE_USER");
        userDataDetails = new UserDataDetails(employee);

        List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDataDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testEmptyRole() {
        employee.setEmployeeDesignation("");
        userDataDetails = new UserDataDetails(employee);

        List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDataDetails.getAuthorities();
        assertEquals(1, authorities.size()); // Should be at least one default role
    }
}
