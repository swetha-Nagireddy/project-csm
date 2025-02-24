package com.example.demo.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.TicketNotFoundException;
import com.example.demo.model.Faq;
import com.example.demo.model.Ticket;
import com.example.demo.repo.FaqRepository;
import com.example.demo.repo.TicketRepository;

/**
 * Service Class
 * This Class contains all business logic implementations for Chatbot functionalities
 * 
 * @author Manoj.KS
 */


@Service
public class ChatbotService {
	
    private final TicketRepository ticketRepository;
    private final FaqRepository faqRepository;

    /**
     * Constructor for injecting dependencies into ChatbotService.
     * 
     * @param ticketRepository the repository to access ticket data.
     * @param faqRepository the repository to access FAQ data.
     */
    
    public ChatbotService(TicketRepository ticketRepository, FaqRepository faqRepository) {
        this.ticketRepository = ticketRepository;
		this.faqRepository = faqRepository;
    }
 
    private static final Logger logger = Logger.getLogger(ChatbotService.class);
    
    /**
     * Retrieves the status of all tickets associated with the provided customer ID.
     * If no tickets are found for the given customer ID, a TicketNotFoundException is thrown.
     */
    
    public String getTicketStatus(int customerId) {
        List<Ticket> tickets = ticketRepository.findByCustomer_CustomerId(customerId);
        if (tickets.isEmpty()) {
        	logger.warn("No tickets found for customer Id: " +  customerId);
            throw new TicketNotFoundException("No tickets found for customer ID " + customerId);
        }

        logger.info("Found " + tickets.size() + " tickets for customer ID: " + customerId);
        
        StringBuilder message = new StringBuilder("Your tickets: \n");
        for (Ticket ticket : tickets) {
            message.append("Ticket ID: ").append(ticket.getTicketId()).append("\n")
                         .append(", Status: ").append(ticket.getTicketStatus()).append("\n")
                         .append(", Reason : ").append(ticket.getTicketType()).append("\n")
                         .append(", Description : ").append(ticket.getTicketDescription()).append("\n")
                         .append("\n");
        }
        logger.info("Successfully generated ticket status message for customer ID: " +  customerId);
        return message.toString();
    }
    
    /**
     * Fetches all frequently asked questions (FAQs) from the FAQ repository.
     * @return A list of all FAQ objects.
     */

    public List<Faq> getAllFaqs() {
        List<Faq> faqs = faqRepository.findAll();
        logger.info("Fetched all FAQs");
        return faqs;
    }
}