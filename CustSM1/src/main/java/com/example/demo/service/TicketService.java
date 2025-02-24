package com.example.demo.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.DuplicateTicketException;
import com.example.demo.exceptions.EmployeeNotFoundException;
import com.example.demo.exceptions.InvalidTicketStatusException;
import com.example.demo.exceptions.TicketNotFoundException;
import com.example.demo.model.Constants;
import com.example.demo.model.Customer;
import com.example.demo.model.Employee;
import com.example.demo.model.Ticket;
import com.example.demo.repo.CustomerRepository;
import com.example.demo.repo.EmployeeRepository;
import com.example.demo.repo.TicketRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * TicketService class handles the business logic related to ticket management such as creating, updating, 
 * closing, reopening, and searching tickets. It also assigns employees to tickets based on various criteria 
 * like ticket type, priority, and employee workload. Additionally, it sends email notifications to customers 
 * about ticket updates.
 *
 * @author Srihari.P, Manjunath.AS, Satwik.A
 */

@Service
@Transactional
public class TicketService {
    
     private final TicketRepository ticketRepository;
	 private final CustomerRepository customerRepository;
	 private final EmailService emailService;
	 private EmployeeRepository employeeRepository;

	    public TicketService(TicketRepository ticketRepository, CustomerRepository customerRepository,
	    		EmailService emailService, EmployeeRepository employeeRepository) {
	    	this.ticketRepository = ticketRepository;
	    	this.customerRepository = customerRepository;
			this.emailService = emailService;
			this.employeeRepository = employeeRepository;
	    }
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private static Logger logger = Logger.getLogger(TicketService.class);
    
    private static final String TICKET_MESSAGE = "ticket Count:";
    
    private static final String EMPLOYEE_ID_STRING="employeeId";

    private static final String ERROR_MESSAGE = "Ticket Not Found with ID: ";

    private int roundRobinIndex = 0;
    
    /**
     * Adds a new ticket to the system and assigns an employee based on workload.
     * Ensures there are no duplicate tickets for the customer with the same type.
     * Sends a notification email to the customer once the ticket is raised.
     * @throws DuplicateTicketException - Thrown if a duplicate ticket already exists.
     */
    
    public Ticket addTicket(Ticket ticket) {
    	
    	logger.info("Adding new ticket for customer ID: "+ ticket.getCustomer().getCustomerId());
    	
    	 if (ticket.getCustomer() == null) {
    	        logger.error("Attempt to create ticket without customer information");
    	        throw new IllegalArgumentException("Ticket must have a customer associated with it");
    	    }
    	 
    	List<Ticket> existingTickets = ticketRepository.findByCustomer_CustomerIdAndTicketTypeAndTicketStatusIn(
    	        ticket.getCustomer().getCustomerId(),
    	        ticket.getTicketType(),
    	        Arrays.asList(Constants.PENDING, Constants.OPEN)
    	    );

    	    if (!existingTickets.isEmpty()) {
    	    	logger.warn("Duplicate ticket found for customer ID:" + ticket.getCustomer().getCustomerId() + "and ticket type: " + ticket.getTicketType());
    	        throw new DuplicateTicketException("A ticket of type " + ticket.getTicketType() + 
    	            " is already open. Please wait until it is resolved.");
    	    }
  
        // Set ticket priority   
        ticket.setTicketPriority(assignPriority(ticket.getTicketType()));
        
        // Set ticket status to "Pending" by default
        ticket.setTicketStatus(Constants.PENDING);

        if (ticket.getCustomer() == null) {
        	logger.error("Attempt to create ticket without customer information");
            throw new IllegalArgumentException("Ticket must have a customer associated with it");
        }

        if (ticket.getTicketRaiseDate() == null) {
            ticket.setTicketRaiseDate(LocalDateTime.now());
        }

        Customer customer = customerRepository.findById(ticket.getCustomer().getCustomerId())
                .orElseThrow(() -> {
                    logger.error("Customer not found with ID: {}" + ticket.getCustomer().getCustomerId());
                    return new IllegalArgumentException("Customer not found");
                });
        
        ticket.setCustomer(customer);
        logger.info("Customer details verified for ticket creation");
        

        // Get all employees with "Employee" designation from the specific department
        
        List<Employee> departmentEmployees = employeeRepository.findByEmployeeDesignation("Employee");
        if (departmentEmployees.isEmpty()) {
            logger.warn("No employees found with 'Employee' designation");
        }
        
        // Filter employees by department
        List<Integer> eligibleEmployees = departmentEmployees.stream()
                .filter(emp -> emp.getEmployeeDept().equals(ticket.getTicketType()))
                .map(Employee::getEmployeeId)
                .toList(); 

        if (eligibleEmployees.isEmpty()) {
            throw new EmployeeNotFoundException("No employees with 'Employee' designation available for ticket type: " + ticket.getTicketType());
        }
        

        // Find employees with the least number of total tickets
        Integer minTicketsEmployee = eligibleEmployees.stream()
                .min(Comparator.comparingInt(ticketRepository::countByEmployeeId))
                .orElse(null);

        List<Integer> leastLoadedEmployees = eligibleEmployees.stream()
                .filter(empId -> ticketRepository.countByEmployeeId(empId)
                        == ticketRepository.countByEmployeeId(minTicketsEmployee))
                .toList();

        // Find employees with the least number of OPEN tickets
        Integer minOpenTicketsEmployee = leastLoadedEmployees.stream()
                .min(Comparator.comparingInt(empId -> ticketRepository.countByEmployeeIdAndTicketStatus(empId, "OPEN")))
                .orElse(null);

        List<Integer> leastOpenEmployees = leastLoadedEmployees.stream()
                .filter(empId -> ticketRepository.countByEmployeeIdAndTicketStatus(empId, Constants.OPEN)
                        == ticketRepository.countByEmployeeIdAndTicketStatus(minOpenTicketsEmployee, Constants.OPEN))
                .toList();

        // Assign based on priority distribution
        Integer assignedEmployee = leastOpenEmployees.stream()
                .min(Comparator.comparingInt(empId -> ticketRepository.countByEmployeeIdAndTicketPriority(empId, "LOW")))
                .orElseGet(() -> leastOpenEmployees.stream()
                        .min(Comparator.comparingInt(empId -> ticketRepository.countByEmployeeIdAndTicketPriority(empId, "MEDIUM")))
                        .orElse(null));

        // Apply round-robin if all conditions match
        if (assignedEmployee == null) {
            int nextIndex = (roundRobinIndex % leastOpenEmployees.size());
            assignedEmployee = leastOpenEmployees.get(nextIndex);
            roundRobinIndex++;
        }

        // Assign ticket to the selected employee
        ticket.setEmployeeId(assignedEmployee);

        // Save and return the ticket
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket successfully created with ID: {}"+ savedTicket.getTicketId());
        
        // Send email notification to the customer
        String emailSubject = "Ticket Raised Successfully";
        String emailBody = "Dear " + ticket.getCustomer().getCustomerFirstname() + ",\n\n" +
                "Your ticket has been successfully raised. Our team will get back to you shortly.\n\n" +
                "Ticket Details:\n" +
                "Ticket ID: " + ticket.getTicketId() + "\n" +
                "Assigned Employee ID: " + assignedEmployee + "\n" +
                "Issue: " + ticket.getTicketDescription() + "\n\n" +
                "Thank you for contacting us.";

        emailService.sendEmail(ticket.getCustomer().getCustomerEmail(), emailSubject, emailBody);

        return savedTicket;
    }
    
    // Set priority and status for the ticket
    public String assignPriority(String ticketType) {
        return switch (ticketType) {
            case "OUTAGE" -> "HIGH";
            case "INSTALLATION_AND_SERVICE", "TECHNICAL_SUPPORT", "RELOCATION_REQUEST" -> "MEDIUM";
            case "BILLING_AND_ACCOUNTS" ,"PRODUCT_AND_PLANS"-> "LOW";
            case "OTHER" -> "LOW";
            default -> "LOW";
        };
    }
    
    /**
     * Fetches and returns all the tickets in the system.
     */
    
    public List<Ticket> showTicket() {
    	logger.info("Fetching all tickets");
        return ticketRepository.findAll();
    }
    
    /**
     * Searches and returns a ticket by its unique ticket ID. If not found, throws TicketNotFoundException.
     */
    
    public Ticket searchTicketByTicketId(int ticketId) {
    	logger.info("Searching for ticket with Id: "+ ticketId);
    	Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        
        // If the ticket is not found, throw TicketNotFoundException
        if (ticket.isEmpty()) {
        	logger.error(ERROR_MESSAGE+ ticketId);
            throw new TicketNotFoundException(ERROR_MESSAGE + ticketId);
        }
        logger.info("Found ticket:"+ ticket.get());
        // Return the found ticket
        return ticket.get();     
    }
    
    /**
     * Searches and returns a list of tickets for a given customer ID.
     */
    
    public List<Ticket> searchTicketByCustomerId(int customerId) {
    	logger.info("Searching tickets for customer ID: {}"+ customerId);
        List<Ticket> tickets= ticketRepository.findByCustomer_CustomerId(customerId);
        logger.info("Found"+tickets.size()+"tickets for customer ID:"+ customerId);
        return tickets;
    }
    
    /**
     * Searches and returns a list of tickets assigned to a given employee ID.
     */
    
    public List<Ticket> searchTicketByEmployeeId(int employeeId) {
    	logger.info("Searching tickets for employee ID: "+ employeeId);
        List<Ticket> tickets =  ticketRepository.findByEmployeeId(employeeId);
        logger.info("Found" +tickets.size() + "tickets for employee ID:"+employeeId);
        return tickets;
    }
    
    
    /**
     * Updates the details of an existing ticket. If the ticket is closed, it updates its resolution time and calculates 
     * turnaround time. It also triggers an email notification to the customer about the ticket update.
     */
    
    public Ticket updateTicketDetails(int ticketId, Ticket updatedTicket) {
        logger.info("Updating ticket with ID: " + ticketId);
        
        Ticket existingTicket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String customerEmail = existingTicket.getCustomer().getCustomerEmail();

        boolean isStatusUpdated = trackStatusChange(existingTicket, updatedTicket);
        boolean isTicketTypeUpdated = !existingTicket.getTicketType().equals(updatedTicket.getTicketType());
        boolean isTicketPriorityUpdated = !existingTicket.getTicketPriority().equals(updatedTicket.getTicketPriority());
        boolean isRatingOrFeedbackUpdated = isFeedbackUpdated(existingTicket, updatedTicket);
        boolean isStatusChangedToClosed = Constants.CLOSED.equalsIgnoreCase(updatedTicket.getTicketStatus());

        String oldStatus = existingTicket.getTicketStatus();
        String oldTicketType = existingTicket.getTicketType();
        String oldTicketPriority = existingTicket.getTicketPriority();

        boolean isEmployeeUpdate = isStatusUpdated || isTicketTypeUpdated || isTicketPriorityUpdated;
        boolean isCustomerUpdate = isRatingOrFeedbackUpdated && !isEmployeeUpdate;

        if (isEmployeeUpdate) {
            processEmployeeUpdates(existingTicket, updatedTicket, isStatusUpdated, isTicketTypeUpdated, isStatusChangedToClosed);
        }

        if (isCustomerUpdate) {
            processCustomerUpdates(existingTicket, updatedTicket);
        }

        // Save and return the updated ticket, send email notifications
        Ticket savedTicket = ticketRepository.save(existingTicket);
        logger.info("Ticket successfully closed by customer, ID: "+ ticketId);
        
        if (isEmployeeUpdate && isStatusUpdated) {
            emailService.sendTicketUpdateEmail(
                    customerEmail,
                    savedTicket.getTicketId(),
                    oldStatus, savedTicket.getTicketStatus(),
                    oldTicketType, savedTicket.getTicketType(),
                    oldTicketPriority
            );
        }

        if (isEmployeeUpdate && isStatusChangedToClosed) {
            emailService.sendClosedTicketEmail(
                    customerEmail,
                    savedTicket.getTicketId(),
                    updatedTicket.getEmployeeComment()
            );
        }
        
        return savedTicket;
    }

    /**
     *Checks whether the ticket status has been changed (Helper method for updateTicket)
     */
    
    private boolean trackStatusChange(Ticket existingTicket, Ticket updatedTicket) {
        boolean isStatusUpdated = !existingTicket.getTicketStatus().equals(updatedTicket.getTicketStatus());
        if (isStatusUpdated) {
            logger.info("Ticket status changing from " + existingTicket.getTicketStatus() + " to " + updatedTicket.getTicketStatus());
        }
        return isStatusUpdated;
    }

    /**
     *Customer feedback and rating update (Helper method for updateTicket)
     */
    
    private boolean isFeedbackUpdated(Ticket existingTicket, Ticket updatedTicket) {
        return !Objects.equals(existingTicket.getCustomerRating(), updatedTicket.getCustomerRating()) ||
               !Objects.equals(existingTicket.getCustomerFeedback(), updatedTicket.getCustomerFeedback());
    }

    /**
     *Updates on ticket by employee on ticket type, priority or to reassign ticket to another employee (Helper method for updateTicket)
     */
    
    private void processEmployeeUpdates(Ticket existingTicket, Ticket updatedTicket, boolean isStatusUpdated, boolean isTicketTypeUpdated, boolean isStatusChangedToClosed) {
        existingTicket.setTicketStatus(updatedTicket.getTicketStatus());

        if (isTicketTypeUpdated) {
            existingTicket.setTicketType(updatedTicket.getTicketType());
            existingTicket.setTicketPriority(assignPriority(updatedTicket.getTicketType()));
            reassignTicketToEmployee(existingTicket);
        }

        if (isStatusUpdated && existingTicket.getResponseTime() == null) {
            existingTicket.setResponseTime(LocalDateTime.now());
        }

        if (isStatusChangedToClosed) {
            existingTicket.setResolveTime(LocalDateTime.now());
            existingTicket.setTurnAroundTime(calculateTurnAroundTime(existingTicket.getTicketRaiseDate(), existingTicket.getResolveTime()));
            existingTicket.setEmployeeComment(updatedTicket.getEmployeeComment());
        }
    }

    public void processCustomerUpdates(Ticket existingTicket, Ticket updatedTicket) {
        existingTicket.setCustomerRating(updatedTicket.getCustomerRating());
        existingTicket.setCustomerFeedback(updatedTicket.getCustomerFeedback());
    }

    /**
     * Reassigns a ticket to an employee based on the ticket type and employee workload.
     */
    
    public void reassignTicketToEmployee(Ticket ticket) {
        List<Employee> eligibleEmployees = new ArrayList<>();
        
        for (Employee emp : employeeRepository.findByEmployeeDesignation("Employee")) {
            if (emp.getEmployeeDept().equals(ticket.getTicketType())) {
                eligibleEmployees.add(emp);
            }
        }

        if (!eligibleEmployees.isEmpty()) {
            Integer reassignedEmployee = null;
            int minTickets = Integer.MAX_VALUE;

            for (Employee emp : eligibleEmployees) {
                int ticketCount = ticketRepository.countByEmployeeId(emp.getEmployeeId());
                if (ticketCount < minTickets) {
                    minTickets = ticketCount;
                    reassignedEmployee = emp.getEmployeeId();
                }
            }
            

            if (reassignedEmployee != null) {
                ticket.setEmployeeId(reassignedEmployee);
            }
        }
    }

    /** Calculate turnaround time in days:hours:min:seconds format
     */
    
    public String calculateTurnAroundTime(LocalDateTime raiseDate, LocalDateTime resolveDate) {
    	if (raiseDate == null || resolveDate == null) {
            return("Both startInclusive and endExclusive must not be null");
        }
        Duration duration = Duration.between(raiseDate, resolveDate);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%d days %d hours %d min %d sec", days, hours, minutes, seconds);
    }
    
    /**
     * Closes the ticket by the customer, marking it as resolved and setting the resolution time.
     * @throws TicketNotFoundException - Thrown if the ticket is not found.
     * @throws InvalidTicketStatusException - Thrown if the ticket is already closed.
     */
    
    public Ticket closeTicketByCustomer(int ticketId) {
        Ticket existingTicket = ticketRepository.findById(ticketId).orElse(null);
 
        if (existingTicket == null) {
            throw new TicketNotFoundException(ERROR_MESSAGE + ticketId);
        }
 
        if (Constants.CLOSED.equals(existingTicket.getTicketStatus())) {
            throw new InvalidTicketStatusException("Ticket is already closed.");
        }
 
        existingTicket.setTicketStatus(Constants.CLOSED);
        existingTicket.setResolveTime(LocalDateTime.now());
 
        return ticketRepository.save(existingTicket);
    }
 
    /**
     * Reopens a previously closed ticket, creating a new ticket based on the existing ticket's details.
     * @throws TicketNotFoundException - Thrown if the ticket is not found.
     * @throws InvalidTicketStatusException - Thrown if the ticket is not closed.
     */
    
    public Ticket reopenTicket(int ticketId) {
        // Get the existing ticket
        Ticket existingTicket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + ticketId));

        // Check if ticket is NOT in CLOSED status, throw exception if it isn't
        if (!Constants.CLOSED.equals(existingTicket.getTicketStatus())) {
            throw new InvalidTicketStatusException("Only closed tickets can be reopened.");
        }

        // Ensure ticketType is not null
        if (existingTicket.getTicketType() == null) {
            throw new IllegalArgumentException("Ticket type cannot be null");
        }

        // Create new ticket with copied data
        Ticket newTicket = new Ticket();
        newTicket.setCustomer(existingTicket.getCustomer());
        newTicket.setTicketType(existingTicket.getTicketType());
        newTicket.setTicketDescription(existingTicket.getTicketDescription());
        newTicket.setTicketRaiseDate(LocalDateTime.now());
        newTicket.setTicketStatus(Constants.PENDING); // Set status to PENDING, not CLOSED
        newTicket.setTicketPriority(existingTicket.getTicketPriority());

        // Assign an employee based on ticket type
        newTicket.setEmployeeId(assignEmployeeForTicketType(existingTicket.getTicketType()));

        // Save and return the new ticket
        Ticket savedTicket = ticketRepository.save(newTicket);

        // Send email notification to the customer
        String emailSubject = "Your Ticket has been Reopened Successfully";
        String emailBody = "Dear " + newTicket.getCustomer().getCustomerFirstname() + ",\n\n" +
                "Your ticket has been successfully reopened. Our team will get back to you shortly.\n\n" +
                "Ticket Details:\n" +
                "New Ticket ID: " + newTicket.getTicketId() + "\n" +
                "Original Ticket ID: " + ticketId + "\n" +
                "Issue: " + newTicket.getTicketDescription() + "\n\n" +
                "Thank you for contacting us.";

        emailService.sendEmail(newTicket.getCustomer().getCustomerEmail(), emailSubject, emailBody);

        return savedTicket;
    }
 
    /**
     * Assigns an employee to a ticket based on its type and availability of employees.
     */
    
    private int assignEmployeeForTicketType(String ticketType) {
        
        List<Employee> availableEmployees = employeeRepository.findByEmployeeDept(ticketType);
        if (availableEmployees.isEmpty()) {
            throw new EmployeeNotFoundException("No employees available for ticket type: " + ticketType);
        }
        
        // For simplicity, assign to the first available employee
        // In a real implementation, you might want to use a more sophisticated assignment algorithm
        return availableEmployees.get(0).getEmployeeId();
    }
    
    
  //location state vs no.of tickets
    public List<Map<String, Object>> getTicketCountByLocation() {
        List<Object[]> results = ticketRepository.getTicketCountByLocation();
        List<Map<String, Object>> data = new ArrayList<>();
 
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("location", row[0]);
            map.put(TICKET_MESSAGE, row[1]);
            data.add(map);
        }
        return data;
    }
    
 
    // City vs. No. of Tickets
    public List<Map<String, Object>> getTicketCountByCity() {
        // Assuming `ticketRepository.getTicketCountByCity()` is a valid repository method
        List<Object[]> results = ticketRepository.getTicketCountByCity();
        List<Map<String, Object>> data = new ArrayList<>();
     
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("city", row[0]);  // Change "location" to "city"
            map.put(TICKET_MESSAGE, row[1]);
            data.add(map);
        }
        return data;
    }
 
    //domain vs tickets
    public List<Map<String, Object>> getTicketCountByDept() {
        List<Object[]> results = ticketRepository.countTicketsByEmployeeDept();
        List<Map<String, Object>> formattedResults = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("employeeDept", row[0]);
            map.put(TICKET_MESSAGE, row[1]);
            formattedResults.add(map);
        }
        
        return formattedResults;
    }
    
    
    // Get count of Open tickets
    public long getOpenTicketCount() {
        return ticketRepository.countTicketsByOpenStatus();
    }
 
    // Get count of Closed tickets
    public long getClosedTicketCount() {
        return ticketRepository.countTicketsByClosedStatus();
    }
 
    // Get count of In Progress tickets
    public long getPendingTicketCount() {
        return ticketRepository.countTicketsByPendingStatus();
    }
    
 
    //avg response of each employee under respective manager
    public List<Map<String, Object>> getAvgResponseTimeByManager(int managerId) {
        List<Object[]> result = ticketRepository.findAvgResponseTimeByManager(managerId);
 
        List<Map<String, Object>> responseList = new ArrayList<>();
 
        for (Object[] row : result) {
            // Ensure safe extraction and casting
            Integer employeeId = (Integer) row[0];  // Cast employeeId to Integer
            Integer managerIdFromDB = (Integer) row[1];  // Cast managerId to Integer
            Double avgResponseTime = (Double) row[2];  // Cast avgResponseTime to Double
 
            // Create the map to store the result
            Map<String, Object> map = new HashMap<>();
            map.put(EMPLOYEE_ID_STRING, employeeId);
            map.put("managerId", managerIdFromDB);
            map.put("avgResponseTime", avgResponseTime);
 
            responseList.add(map);
        }
 
        return responseList;
    }
   
    //avg resolution time of each employee under respective manager
    public List<Map<String, Object>> getAvgResolutionTimesByManager(Integer managerId) {
        // Get the list of employees under the manager
        List<Employee> employees = employeeRepository.findByEmployeeManagerId(managerId);
        
        // Extract employee IDs
        List<Integer> employeeIds = employees.stream().map(Employee::getEmployeeId).toList();
        
        // Fetch average resolution times for employees under this manager
        List<Object[]> results = ticketRepository.getAvgResolutionTimesForEmployees(employeeIds);
        
        // List to store the formatted data
        List<Map<String, Object>> responseList = new ArrayList<>();
        
        for (Object[] result : results) {
            Integer employeeId = (Integer) result[0];
            Double avgResolutionTime = (Double) result[1];
            
            // Create a map for each employee
            Map<String, Object> employeeData = new HashMap<>();
            employeeData.put(EMPLOYEE_ID_STRING, employeeId);
            employeeData.put("avgResolutionTime", avgResolutionTime);
            employeeData.put("managerId", managerId); // Add the managerId for reference
            
            // Add to the response list
            responseList.add(employeeData);
        }
 
        return responseList;  // Return the list of maps
    }
    
    // Get the count of tickets for employees under a specific manager
    public long getTicketCountForManager(int managerId) {
        return ticketRepository.countTicketsByManagerId(managerId);
    }
    
    
    // Fetch the average response time for a specific employee and return as a list of maps
    public List<Map<String, Object>> getAverageResponseTimeForEmployee(int employeeId) {
        // Fetch raw data from the repository
        List<Object[]> results = ticketRepository.getAvgResponseTimeForEmployee(employeeId);
       
        List<Map<String, Object>> formattedResults = new ArrayList<>();
       
        if (!results.isEmpty()) {
            // Loop through the raw results and format into a Map
            for (Object[] result : results) {
                Map<String, Object> employeeData = new HashMap<>();
                employeeData.put(EMPLOYEE_ID_STRING, result[0]);  // Employee ID
                employeeData.put("avgResponseTime", result[1]);  // Average response time
               
                formattedResults.add(employeeData);
            }
        }
       
        return formattedResults;
    }
   
    // Get average resolution time for a specific employee, grouped by month and year
    public List<Map<String, Object>> getAvgResolutionTimeByEmployeeForMonth(int employeeId) {
        List<Object[]> results = ticketRepository.findAvgResolutionTimeByEmployeeForEachMonth(employeeId);
 
        // List to hold formatted data for React bar chart
        List<Map<String, Object>> formattedData = new ArrayList<>();
 
        // Iterate over query results and format the data for React
        for (Object[] row : results) {
            int month = (int) row[1];
            int year = (int) row[2];
 
            // Convert BigDecimal to Double (handling avgResolutionTime safely)
            BigDecimal avgResolutionTimeBigDecimal = (BigDecimal) row[3];
            double avgResolutionTime = avgResolutionTimeBigDecimal.doubleValue();
 
            Map<String, Object> newEntry = new HashMap<>();
            newEntry.put("month", month);
            newEntry.put("year", year);
            newEntry.put("avgResolutionTime", avgResolutionTime);
            formattedData.add(newEntry);
        }
 
        return formattedData;
    }

    public void deleteTicketByTicketId(int ticketId) {
        ticketRepository.deleteById(ticketId);
        logger.info("Successfully deleted ticket with ID:"+ ticketId);
    }
}