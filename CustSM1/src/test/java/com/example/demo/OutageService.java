package com.example.demo;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
import java.util.List;
 
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import com.example.demo.exceptions.OutageNotFoundException;
import com.example.demo.repo.TicketRepository;
import com.example.demo.service.OutageService;
 
@ExtendWith(MockitoExtension.class)
class OutageServiceTest {
 
    private static final Logger logger = Logger.getLogger(OutageServiceTest.class);
 
    @Mock
    private TicketRepository ticketRepository;
 
    @InjectMocks
    private OutageService outageService;
 
    @BeforeEach
    void setUp() {
        logger.info("Setting up test cases for OutageService...");
    }
 
    @Test
    void getOutageLocations_Success() {
        // Given: A valid list of outage locations
        List<Object[]> outageLocations = List.of(
            new Object[]{"Chennai", 13.0827, 80.2707},
            new Object[]{"Bangalore", 12.9716, 77.5946}
        );
 
        when(ticketRepository.findOutageLocations()).thenReturn(outageLocations);
 
        // When: Calling the method
        List<Object[]> result = outageService.getOutageLocations();
 
        // Then: Ensure the returned list is correct
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Chennai", result.get(0)[0]); // Verify first outage location
        verify(ticketRepository, times(1)).findOutageLocations();
    }
 
    @Test
    void getOutageLocations_EmptyList_ThrowsException() {
        // Given: Repository returns an empty list
        when(ticketRepository.findOutageLocations()).thenReturn(List.of());
 
        // When: Calling the method, expect an exception
        Exception exception = assertThrows(OutageNotFoundException.class, () -> {
            outageService.getOutageLocations();
        });
 
        // Then: Verify exception message and method calls
        assertEquals("No outage locations found.", exception.getMessage());
        verify(ticketRepository, times(1)).findOutageLocations();
    }
 
    
    @Test
    void getOutageLocations_NullList_ThrowsException() {
        // Given: Repository returns null
        when(ticketRepository.findOutageLocations()).thenReturn(null);
 
        // When: Calling the method, expect an exception
        Exception exception = assertThrows(OutageNotFoundException.class, () -> {
            outageService.getOutageLocations();
        });
 
        // Then: Verify exception message and method calls
        assertEquals("No outage locations found.", exception.getMessage());
        verify(ticketRepository, times(1)).findOutageLocations();
    }
}