package com.example.demo;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
 
import org.junit.jupiter.api.Test;
 
import com.example.demo.model.Customer;
 
class CustomerTest {
 
    @Test
    void testCustomerConstructorAndGetters() {
        int customerId = 1;
        String customerFirstname = "Manoj";
        String customerLastname = "Doe";
        String customerAddress = "123 Main St";
        String customerPincode = "123456";
        String customerGender = "Male";
        String customerUsername = "johndoe";
        String customerPassword = "password";
        String customerEmail = "john.doe@example.com";
        String customerPhno = "1234567890";
        double customerLatitude = 12.345678;
        double customerLongitude = 98.765432;
        String customerCity = "CityName";
        String customerState = "StateName";
 
        
        // When
        Customer customer = new Customer(customerId, customerFirstname, customerLastname, customerAddress, customerPincode,
                customerGender, customerUsername, customerPassword, customerEmail, customerPhno, customerLatitude,
                customerLongitude, customerCity, customerState);
 
        // Then
        assertEquals(customerId, customer.getCustomerId());
        assertEquals(customerFirstname, customer.getCustomerFirstname());
        assertEquals(customerLastname, customer.getCustomerLastname());
        assertEquals(customerAddress, customer.getCustomerAddress());
        assertEquals(customerPincode, customer.getCustomerPincode());
        assertEquals(customerGender, customer.getCustomerGender());
        assertEquals(customerUsername, customer.getCustomerUsername());
        assertEquals(customerPassword, customer.getCustomerPassword());
        assertEquals(customerEmail, customer.getCustomerEmail());
        assertEquals(customerPhno, customer.getCustomerPhno());
        assertEquals(customerLatitude, customer.getCustomerLatitude());
        assertEquals(customerLongitude, customer.getCustomerLongitude());
        assertEquals(customerCity, customer.getCustomerCity());
        assertEquals(customerState, customer.getCustomerState());
    }
 
    @Test
    void testSetters() {
        // Given
        Customer customer = new Customer();
 
        // When
        customer.setCustomerId(1);
        customer.setCustomerFirstname("Manoj");
        customer.setCustomerLastname("Doe");
        customer.setCustomerAddress("123 Main St");
        customer.setCustomerPincode("123456");
        customer.setCustomerGender("Male");
        customer.setCustomerUsername("johndoe");
        customer.setCustomerPassword("password");
        customer.setCustomerEmail("john.doe@example.com");
        customer.setCustomerPhno("1234567890");
        customer.setCustomerLatitude(12.345678);
        customer.setCustomerLongitude(98.765432);
        customer.setCustomerCity("CityName");
        customer.setCustomerState("StateName");
 
        // Then
        assertEquals(1, customer.getCustomerId());
        assertEquals("Manoj", customer.getCustomerFirstname());
        assertEquals("Doe", customer.getCustomerLastname());
        assertEquals("123 Main St", customer.getCustomerAddress());
        assertEquals("123456", customer.getCustomerPincode());
        assertEquals("Male", customer.getCustomerGender());
        assertEquals("johndoe", customer.getCustomerUsername());
        assertEquals("password", customer.getCustomerPassword());
        assertEquals("john.doe@example.com", customer.getCustomerEmail());
        assertEquals("1234567890", customer.getCustomerPhno());
        assertEquals(12.345678, customer.getCustomerLatitude());
        assertEquals(98.765432, customer.getCustomerLongitude());
        assertEquals("CityName", customer.getCustomerCity());
        assertEquals("StateName", customer.getCustomerState());
    }
 
    @Test
    void testDefaultConstructor() {
        // Given
        Customer customer = new Customer();
 
        // Then
        assertNotNull(customer);
        assertEquals(0, customer.getCustomerId()); // Default value for int
        assertNull(customer.getCustomerFirstname());
        assertNull(customer.getCustomerLastname());
        assertNull(customer.getCustomerAddress());
        assertNull(customer.getCustomerPincode());
        assertNull(customer.getCustomerGender());
        assertNull(customer.getCustomerUsername());
        assertNull(customer.getCustomerPassword());
        assertNull(customer.getCustomerEmail());
        assertNull(customer.getCustomerPhno());
        assertEquals(0.0, customer.getCustomerLatitude());
        assertEquals(0.0, customer.getCustomerLongitude());
        assertNull(customer.getCustomerCity());
        assertNull(customer.getCustomerState());
    }
}
 
 
 