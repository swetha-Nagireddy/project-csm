package com.example.demo;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
 
import com.example.demo.exceptions.TicketNotFoundException;
import com.example.demo.model.Customer;
import com.example.demo.model.Faq;
import com.example.demo.model.Ticket;
import com.example.demo.repo.FaqRepository;
import com.example.demo.repo.TicketRepository;
import com.example.demo.service.ChatbotService;
 
class FaqServiceTest {
 
    @InjectMocks
    private ChatbotService chatbotService;  // Service to be tested
 
    @Mock
    private TicketRepository ticketRepository;  // Mocking the TicketRepository
 
    @Mock
    private FaqRepository faqRepository;  // Mocking the FaqRepository
 
    @Mock
    private Customer customer;  // Mocking the Customer object
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mock objects
    }
 
    @Test
    void testGetTicketStatus_whenTicketsExist() {
        // Arrange: Prepare mock data for tickets
        int customerId = 123;
        List<Ticket> mockTickets = new ArrayList<>();
 
        // Create Ticket 1 with setters
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId(101);
        ticket1.setEmployeeId(1001);
        ticket1.setTicketType("Technical");
        ticket1.setTicketDescription("System not working");
        ticket1.setTicketRaiseDate(LocalDateTime.of(2025, 2, 14, 10, 30));
        ticket1.setTicketStatus("Open");
        ticket1.setTicketPriority("High");
        ticket1.setResponseTime(LocalDateTime.of(2025, 2, 14, 11, 0));
        ticket1.setResolveTime(LocalDateTime.of(2025, 2, 15, 15, 30));
        ticket1.setEmployeeComment("Issue escalated");
        ticket1.setTurnAroundTime("5 hours");
        ticket1.setCustomerRating(4);
        ticket1.setCustomerFeedback("Satisfied with the response");
        ticket1.setCustomer(customer);  // Assuming customer is mocked
 
        // Create Ticket 2 with setters
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId(102);
        ticket2.setEmployeeId(1002);
        ticket2.setTicketType("Technical");
        ticket2.setTicketDescription("Network issue");
        ticket2.setTicketRaiseDate(LocalDateTime.of(2025, 2, 15, 9, 0));
        ticket2.setTicketStatus("Closed");
        ticket2.setTicketPriority("Medium");
        ticket2.setResponseTime(LocalDateTime.of(2025, 2, 15, 9, 30));
        ticket2.setResolveTime(LocalDateTime.of(2025, 2, 16, 10, 0));
        ticket2.setEmployeeComment("Issue resolved");
        ticket2.setTurnAroundTime("24 hours");
        ticket2.setCustomerRating(5);
        ticket2.setCustomerFeedback("Excellent support");
        ticket2.setCustomer(customer);  // Assuming customer is mocked
 
        mockTickets.add(ticket1);
        mockTickets.add(ticket2);
 
        // Mock the repository call
        when(ticketRepository.findByCustomer_CustomerId(customerId)).thenReturn(mockTickets);
 
        // Act: Call the service method
        String result = chatbotService.getTicketStatus(customerId);
 
        // Assert: Verify the result
        assertNotNull(result);
        assertTrue(result.contains("Ticket ID: 101"));
        assertTrue(result.contains("Status: Open"));
        assertTrue(result.contains("Ticket ID: 102"));
        assertTrue(result.contains("Status: Closed"));
    }
 
    @Test
    void testGetTicketStatus_whenNoTicketsFound() {
        // Arrange
        int customerId = 1;
        when(ticketRepository.findByCustomer_CustomerId(customerId)).thenReturn(new ArrayList<>());
 
        // Act & Assert
        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            chatbotService.getTicketStatus(customerId);
        });
 
        assertEquals("No tickets found for customer ID 1", exception.getMessage());
    }
 
    @Test
    void testGetAllFaqs() {
        // Arrange: Prepare mock FAQ data
        List<Faq> mockFaqs = new ArrayList<>();
        Faq faq1 = new Faq(1L, "How can I check my internet speed?",
				"You can check your internet speed using online tools like Speedtest.net or through our mobile app under the \"Speed Test\" section.");
        Faq faq2 = new Faq(2L, "How do I reset my router?",
				"Unplug your router from the power source, wait for 30 seconds, and plug it back in. Wait for the lights to stabilize before reconnecting.");
        mockFaqs.add(faq1);
        mockFaqs.add(faq2);
 
        when(faqRepository.findAll()).thenReturn(mockFaqs);
 
        // Act: Call the service method
        List<Faq> result = chatbotService.getAllFaqs();
 
        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("How can I check my internet speed?", result.get(0).getQuestion());
        assertEquals("How do I reset my router?", result.get(1).getQuestion());
    }
}