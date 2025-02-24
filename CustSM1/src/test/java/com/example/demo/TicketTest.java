package com.example.demo;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
 
import java.time.LocalDateTime;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
 
import com.example.demo.model.Customer;
import com.example.demo.model.Ticket;
 
class TicketTest {
 
    private Ticket ticket;
    private Customer customer;
 
    
    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerFirstname("John");
        customer.setCustomerLastname("Doe");
        customer.setCustomerUsername("john.doe");
        customer.setCustomerEmail("john@example.com");
        
        ticket = new Ticket();
        ticket.setTicketId(101);
        ticket.setEmployeeId(1001);
        ticket.setTicketType("Technical");
        ticket.setTicketDescription("System not working");
        ticket.setTicketRaiseDate(LocalDateTime.of(2025, 2, 14, 10, 30));
        ticket.setTicketStatus("Open");
        ticket.setTicketPriority("High");
        ticket.setResponseTime(LocalDateTime.of(2025, 2, 14, 11, 0));
        ticket.setResolveTime(LocalDateTime.of(2025, 2, 15, 15, 30));
        ticket.setEmployeeComment("Issue escalated");
        ticket.setTurnAroundTime("5 hours");
        ticket.setCustomerRating(4);
        ticket.setCustomerFeedback("Satisfied with the response");
        ticket.setCustomer(customer);
    }
 
    @Test
    @DisplayName("Test Getters and Setters of Ticket Model")
    void testGettersAndSetters() {
        assertEquals(101, ticket.getTicketId(), "Ticket ID should be 101");
        assertEquals(1001, ticket.getEmployeeId(), "Employee ID should be 1001");
        assertEquals("Technical", ticket.getTicketType(), "Ticket Type should be Technical");
        assertEquals("System not working", ticket.getTicketDescription(), "Description should match");
        assertEquals(LocalDateTime.of(2025, 2, 14, 10, 30), ticket.getTicketRaiseDate(), "Raise Date should match");
        assertEquals("Open", ticket.getTicketStatus(), "Status should be Open");
        assertEquals("High", ticket.getTicketPriority(), "Priority should be High");
        assertEquals(LocalDateTime.of(2025, 2, 14, 11, 0), ticket.getResponseTime(), "Response Time should match");
        assertEquals(LocalDateTime.of(2025, 2, 15, 15, 30), ticket.getResolveTime(), "Resolve Time should match");
        assertEquals("Issue escalated", ticket.getEmployeeComment(), "Comment should match");
        assertEquals("5 hours", ticket.getTurnAroundTime(), "TAT should be 5 hours");
        assertEquals(4, ticket.getCustomerRating(), "Rating should be 4");
        assertEquals("Satisfied with the response", ticket.getCustomerFeedback(), "Feedback should match");
        assertEquals(customer, ticket.getCustomer(), "Customer should match");
    }
 
    @Test
    @DisplayName("Test No-Args Constructor of Ticket Model")
    void testNoArgsConstructor() {
        Ticket newTicket = new Ticket();
        assertNotNull(newTicket, "New Ticket object should not be null");
    }
 
    @Test
    @DisplayName("Test All-Args Constructor of Ticket Model")
    void testAllArgsConstructor() {
        Ticket newTicket = new Ticket(102, 1002, "Service", "Network issue",
                LocalDateTime.of(2025, 2, 13, 9, 0), "Closed", "Medium",
                LocalDateTime.of(2025, 2, 13, 10, 0), LocalDateTime.of(2025, 2, 14, 14, 0),
                "Resolved successfully", "24 hours", 5, "Excellent support", customer);
        
        assertEquals(102, newTicket.getTicketId(), "Ticket ID should be 102");
        assertEquals(1002, newTicket.getEmployeeId(), "Employee ID should be 1002");
        assertEquals("Service", newTicket.getTicketType(), "Type should be Service");
        assertEquals("Network issue", newTicket.getTicketDescription(), "Description should match");
        assertEquals(LocalDateTime.of(2025, 2, 13, 9, 0), newTicket.getTicketRaiseDate(), "Raise Date should match");
        assertEquals("Closed", newTicket.getTicketStatus(), "Status should be Closed");
        assertEquals("Medium", newTicket.getTicketPriority(), "Priority should be Medium");
        assertEquals(LocalDateTime.of(2025, 2, 13, 10, 0), newTicket.getResponseTime(), "Response Time should match");
        assertEquals(LocalDateTime.of(2025, 2, 14, 14, 0), newTicket.getResolveTime(), "Resolve Time should match");
        assertEquals("Resolved successfully", newTicket.getEmployeeComment(), "Comment should match");
        assertEquals("24 hours", newTicket.getTurnAroundTime(), "TAT should be 24 hours");
        assertEquals(5, newTicket.getCustomerRating(), "Rating should be 5");
        assertEquals("Excellent support", newTicket.getCustomerFeedback(), "Feedback should match");
        assertEquals(customer, newTicket.getCustomer(), "Customer should match");
    }
}