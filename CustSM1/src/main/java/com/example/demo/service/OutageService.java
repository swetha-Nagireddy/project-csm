package com.example.demo.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.OutageNotFoundException;
import com.example.demo.repo.TicketRepository;

/**
 * OutageService Class
 * This Class contains all business logic implementations for displaying outage maps
 * 
 * @author Satwik.A 
 */

@Service
public class OutageService {
    
    // Repository to interact with the Ticket data source
	private final TicketRepository ticketRepository;
	
    /**
     * Constructor to inject the TicketRepository dependency.
     */
	
    public OutageService(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}
    
    private static final Logger logger = Logger.getLogger(OutageService.class);
    
    /**
     * Retrieves a list of outage locations from the database.
     * If no outage locations are found, it throws an OutageNotFoundException
     */
    
    public List<Object[]> getOutageLocations() {
        List<Object[]> outageLocations = ticketRepository.findOutageLocations();
        
        // Check if no outage locations are found
        if (outageLocations == null || outageLocations.isEmpty()) {
            logger.warn("No outage locations found in the system");
            throw new OutageNotFoundException("No outage locations found.");
        }
        
        logger.info("Successfully retrieved " + outageLocations.size() + " outage locations");
        return outageLocations;
    }
}
