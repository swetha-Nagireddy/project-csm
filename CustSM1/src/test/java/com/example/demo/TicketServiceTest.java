package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.exceptions.DuplicateTicketException;
import com.example.demo.exceptions.EmployeeNotFoundException;
import com.example.demo.exceptions.InvalidTicketStatusException;
import com.example.demo.exceptions.TicketNotFoundException;
import com.example.demo.model.Customer;
import com.example.demo.model.Employee;
import com.example.demo.model.Ticket;
import com.example.demo.repo.CustomerRepository;
import com.example.demo.repo.EmployeeRepository;
import com.example.demo.repo.TicketRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.TicketService;

 class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TicketService tikcetService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private EmployeeRepository employeeRepository;
    
    @Mock
    private Ticket existticket;

    @Mock
    private Ticket updtticket;
    
    @Mock 
    private Customer cus;
    
    @Mock
    private List<Employee> leastOpenEmployees;

    @Mock
    private Employee assignedEmployee;
    
    
    

    private int roundRobinIndex = 0;

    @InjectMocks
    private TicketService ticketService;
    
    private static final String TICKET_MESSAGE = "ticket Count:";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
     void testShowTicket() {
        // Mock the repository to return a list of tickets
        List<Ticket> mockTickets = new ArrayList<>();
        mockTickets.add(new Ticket());
        when(ticketRepository.findAll()).thenReturn(mockTickets);

        // Call the service method
        List<Ticket> result = ticketService.showTicket();

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findAll();
    }
    
    @Test
    void testSearchTicketByTicketId() {
        // Mock the repository to return an optional ticket
        Ticket mockTicket = new Ticket();
        mockTicket.setTicketId(1);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(mockTicket));

        // Call the service method
        Ticket result = ticketService.searchTicketByTicketId(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.getTicketId());
        verify(ticketRepository, times(1)).findById(1);
    }
    @Test
     void testSearchTicketByCustomerId() {
        // Arrange
        int customerId = 123;
        List<Ticket> mockTickets = new ArrayList<>();
        mockTickets.add(new Ticket()); // Add some mock tickets
        mockTickets.add(new Ticket());

        // Mock the repository to return the mock tickets
        when(ticketRepository.findByCustomer_CustomerId(customerId)).thenReturn(mockTickets);

        // Act
        List<Ticket> result = ticketService.searchTicketByCustomerId(customerId);

        // Assert
        assertEquals(mockTickets, result); // Verify the result is as expected
        verify(ticketRepository, times(1)).findByCustomer_CustomerId(customerId); // Verify the repository method was called
    }

    @Test
    void testSearchTicketByEmployeeId() {
        // Mock the repository to return a list of tickets
        List<Ticket> mockTickets = new ArrayList<>();
        mockTickets.add(new Ticket());
        when(ticketRepository.findByEmployeeId(1)).thenReturn(mockTickets);

        // Call the service method
        List<Ticket> result = ticketService.searchTicketByEmployeeId(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByEmployeeId(1);
    }
    
    @Test
    void testAddTicket_Successful() {
        // Create a Customer object
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerFirstname("John");
        customer.setCustomerEmail("john@example.com");

        // Create a Ticket object and set the Customer
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setTicketType("TECHNICAL_SUPPORT");

        // Mock the repository to return an empty list for existing tickets
        when(ticketRepository.findByCustomer_CustomerIdAndTicketTypeAndTicketStatusIn(
                eq(1), eq("TECHNICAL_SUPPORT"), anyList())).thenReturn(new ArrayList<>());

        // Mock the repository to return the saved ticket
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        // Mock the customer repository to return the customer
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        // Mock the employee repository to return a list of employees
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setEmployeeDept("TECHNICAL_SUPPORT");
        employees.add(employee);
        when(employeeRepository.findByEmployeeDesignation("Employee")).thenReturn(employees);

        // Mock the ticket repository to return counts for employees
        when(ticketRepository.countByEmployeeId(1)).thenReturn(0);
        when(ticketRepository.countByEmployeeIdAndTicketStatus(1, "OPEN")).thenReturn(0);
        when(ticketRepository.countByEmployeeIdAndTicketPriority(1, "LOW")).thenReturn(0);
        
        // Mock the list of employees
        List<Employee> mockLeastOpenEmployees = new ArrayList<>();
        mockLeastOpenEmployees.add(mock(Employee.class));
        mockLeastOpenEmployees.add(mock(Employee.class));
        mockLeastOpenEmployees.add(mock(Employee.class));

        // Set the mock behavior
        when(leastOpenEmployees.size()).thenReturn(mockLeastOpenEmployees.size());
        when(leastOpenEmployees.get(anyInt())).thenReturn(mockLeastOpenEmployees.get(0), mockLeastOpenEmployees.get(1), mockLeastOpenEmployees.get(2));

        // Test the round-robin assignment logic
        assignedEmployee = null;
        int nextIndex = (roundRobinIndex % leastOpenEmployees.size());
        assignedEmployee = leastOpenEmployees.get(nextIndex);
        roundRobinIndex++;

        // Verify the assigned employee is the first in the list
        assertEquals(mockLeastOpenEmployees.get(0), assignedEmployee);

        // Test the next round-robin assignment
        assignedEmployee = null;
        nextIndex = (roundRobinIndex % leastOpenEmployees.size());
        assignedEmployee = leastOpenEmployees.get(nextIndex);
        roundRobinIndex++;

        // Verify the assigned employee is the second in the list
        assertEquals(mockLeastOpenEmployees.get(1), assignedEmployee);

        // Test the next round-robin assignment
        assignedEmployee = null;
        nextIndex = (roundRobinIndex % leastOpenEmployees.size());
        assignedEmployee = leastOpenEmployees.get(nextIndex);
        roundRobinIndex++;

        // Verify the assigned employee is the third in the list
        assertEquals(mockLeastOpenEmployees.get(2), assignedEmployee);

        // Test the next round-robin assignment (should wrap around)
        assignedEmployee = null;
        nextIndex = (roundRobinIndex % leastOpenEmployees.size());
        assignedEmployee = leastOpenEmployees.get(nextIndex);
        roundRobinIndex++;

        // Verify the assigned employee is the first in the list again
       
    

    

        // Call the service method
        Ticket result = ticketService.addTicket(ticket);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.getCustomer().getCustomerId());
        assertEquals("TECHNICAL_SUPPORT", result.getTicketType());
        assertEquals("PENDING", result.getTicketStatus());
        verify(ticketRepository, times(1)).save(ticket);
        assertEquals(mockLeastOpenEmployees.get(0), assignedEmployee);
    }
    
    @Test
     void testAddTicket_DuplicateTicketException() {
        // Create a Customer object
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerFirstname("John");
        customer.setCustomerEmail("john@example.com");

        // Create a Ticket object and set the Customer
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setTicketType("TECHNICAL_SUPPORT");

        // Mock the repository to return a non-empty list for existing tickets
        List<Ticket> existingTickets = new ArrayList<>();
        existingTickets.add(new Ticket());
        when(ticketRepository.findByCustomer_CustomerIdAndTicketTypeAndTicketStatusIn(
                eq(1), eq("TECHNICAL_SUPPORT"), anyList())).thenReturn(existingTickets);

        // Call the service method and expect an exception
        Exception exception = assertThrows(DuplicateTicketException.class, () -> {
            ticketService.addTicket(ticket);
        });

        // Verify the exception message
        assertEquals("A ticket of type TECHNICAL_SUPPORT is already open. Please wait until it is resolved.", exception.getMessage());
    }
    
    @Test
   void testAddTicket_CustomerNotFoundException() {
        // Create a Customer object
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerFirstname("John");
        customer.setCustomerEmail("john@example.com");

        // Create a Ticket object and set the Customer
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setTicketType("TECHNICAL_SUPPORT");

        // Mock the repository to return an empty list for existing tickets
        when(ticketRepository.findByCustomer_CustomerIdAndTicketTypeAndTicketStatusIn(
                eq(1), eq("TECHNICAL_SUPPORT"), anyList())).thenReturn(new ArrayList<>());

        // Mock the customer repository to return an empty optional
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.addTicket(ticket);
        });

        // Verify the exception message
        assertEquals("Customer not found", exception.getMessage());
    }
    @Test
    void testAddTicket_NoEmployeesFoundException() {
        // Create a Customer object
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerFirstname("John");
        customer.setCustomerEmail("john@example.com");

        // Create a Ticket object and set the Customer
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setTicketType("TECHNICAL_SUPPORT");

        // Mock the repository to return an empty list for existing tickets
        when(ticketRepository.findByCustomer_CustomerIdAndTicketTypeAndTicketStatusIn(
                eq(1), eq("TECHNICAL_SUPPORT"), anyList())).thenReturn(new ArrayList<>());

        // Mock the customer repository to return the customer
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        // Mock the employee repository to return an empty list
        when(employeeRepository.findByEmployeeDesignation("Employee")).thenReturn(new ArrayList<>());

        // Call the service method and expect an exception
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            ticketService.addTicket(ticket);
        });

        // Verify the exception message
        assertEquals("No employees with 'Employee' designation available for ticket type: TECHNICAL_SUPPORT", exception.getMessage());
    }
    
    @Test
     void testAddTicket_TicketWithoutCustomer() {
        // Create a Ticket object without setting the Customer
        Ticket ticket = new Ticket();
        ticket.setTicketType("TECHNICAL_SUPPORT");

        // Call the service method and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.addTicket(ticket);
        });

        // Verify the exception message
        assertEquals("Ticket must have a customer associated with it", exception.getMessage());
    }
    
    @Test
     void testUpdateTicketDetails_Successful() {
        // Create a Customer object
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerFirstname("John");
        customer.setCustomerEmail("john@example.com");

        // Create an existing Ticket object
        Ticket existingTicket = new Ticket();
        existingTicket.setTicketId(1);
        existingTicket.setCustomer(customer);
        existingTicket.setTicketType("TECHNICAL_SUPPORT");
        existingTicket.setTicketStatus("PENDING");
        existingTicket.setTicketPriority("MEDIUM"); // Ensure ticketPriority is set

        // Create an updated Ticket object
        Ticket updatedTicket = new Ticket();
        updatedTicket.setTicketType("RELOCATION_REQUEST");
        updatedTicket.setTicketStatus("CLOSED");
        updatedTicket.setTicketPriority("HIGH"); // Ensure ticketPriority is set

        // Mock the repository to return the existing ticket
        when(ticketRepository.findById(1)).thenReturn(Optional.of(existingTicket));

        // Mock the repository to return the saved ticket
        when(ticketRepository.save(existingTicket)).thenReturn(existingTicket);

        // Call the service method
        Ticket result = ticketService.updateTicketDetails(1, updatedTicket);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.getTicketId());
        assertEquals("RELOCATION_REQUEST", result.getTicketType());
        assertEquals("CLOSED", result.getTicketStatus());
        assertEquals("MEDIUM", result.getTicketPriority());
        verify(ticketRepository, times(1)).save(existingTicket);
    }
    
    
    @Test
     void testUpdateTicketDetails_TicketNotFoundException() {
        // Create an updated Ticket object
        Ticket updatedTicket = new Ticket();
        updatedTicket.setTicketType("RELOCATION_REQUEST");
        updatedTicket.setTicketStatus("CLOSED");

        // Mock the repository to return an empty optional
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicketDetails(1, updatedTicket);
        });

        // Verify the exception message
        assertEquals("Ticket not found", exception.getMessage());
    }
    @Test
    void testCloseTicketByCustomer_Successful() {
        // Create a Ticket object
        Ticket ticket = new Ticket();
        ticket.setTicketId(1);
        ticket.setTicketStatus("PENDING");

        // Mock the repository to return the ticket
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        // Call the service method
        Ticket result = ticketService.closeTicketByCustomer(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.getTicketId());
        assertEquals("CLOSED", result.getTicketStatus());
        verify(ticketRepository, times(1)).save(ticket);
    }
    @Test
  void testCloseTicketByCustomer_TicketNotFoundException() {
        // Mock the repository to return an empty optional
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.closeTicketByCustomer(1);
        });

        // Verify the exception message
        assertEquals("Ticket Not Found with ID: 1", exception.getMessage());
    }
    @Test
     void testCloseTicketByCustomer_InvalidTicketStatusException() {
        // Create a Ticket object
        Ticket ticket = new Ticket();
        ticket.setTicketId(1);
        ticket.setTicketStatus("CLOSED");

        // Mock the repository to return the ticket
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        // Call the service method and expect an exception
        Exception exception = assertThrows(InvalidTicketStatusException.class, () -> {
            ticketService.closeTicketByCustomer(1);
        });

        // Verify the exception message
        assertEquals("Ticket is already closed.", exception.getMessage());
    }
    
    @Test
    void testReopenTicket_Successful() {
        // Create an existing Ticket object (initial state)
        Ticket ticket = new Ticket();
        ticket.setTicketId(1);
        ticket.setTicketStatus("CLOSED");
        ticket.setTicketType("TECHNICAL_SUPPORT"); // Ensure ticketType is set

        // Mock the Customer object (avoid NPE when getCustomerFirstname() is called)
        Customer customer = mock(Customer.class);
        when(customer.getCustomerFirstname()).thenReturn("John");
        when(customer.getCustomerEmail()).thenReturn("john.doe@example.com");

        // Assign the mocked customer to the ticket
        ticket.setCustomer(customer);

        // Create the expected new Ticket object (after reopening)
        Ticket newTicket = new Ticket();
        newTicket.setTicketId(2); // A new ticket with a new ID
        newTicket.setTicketStatus("PENDING"); // Status should be PENDING
        newTicket.setTicketType("TECHNICAL_SUPPORT");

        // Mock the repository to return the existing ticket
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        // Mock the repository to return the new ticket when save() is called
        when(ticketRepository.save(any(Ticket.class))).thenReturn(newTicket);

        // Mock the employee repository to return a list of employees
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setEmployeeDept("TECHNICAL_SUPPORT");
        employees.add(employee);
        when(employeeRepository.findByEmployeeDept("TECHNICAL_SUPPORT")).thenReturn(employees);

        // Call the service method
        Ticket result = ticketService.reopenTicket(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.getTicketId());  // New ticket ID
        assertEquals("PENDING", result.getTicketStatus()); // Status should be "PENDING"
        assertEquals("TECHNICAL_SUPPORT", result.getTicketType()); // Ticket type should be the same
        verify(ticketRepository, times(1)).save(any(Ticket.class));  // Ensure save() was called once with any Ticket
    }

    
    @Test
   void testReopenTicket_TicketNotFoundException() {
        // Mock the repository to return an empty optional
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.reopenTicket(1);
        });

        // Verify the exception message
        assertEquals("Ticket not found with ID: 1", exception.getMessage());
    }
    @Test
     void testReopenTicket_InvalidTicketStatusException() {
        // Create a Ticket object
        Ticket ticket = new Ticket();
        ticket.setTicketId(1);
        ticket.setTicketStatus("PENDING");

        // Mock the repository to return the ticket
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        // Call the service method and expect an exception
        Exception exception = assertThrows(InvalidTicketStatusException.class, () -> {
            ticketService.reopenTicket(1);
        });

        // Verify the exception message
        assertEquals("Only closed tickets can be reopened.", exception.getMessage());
    }
    
    @Test
     void testAssignPriority() {
        // Test different ticket types
        assertEquals("HIGH", ticketService.assignPriority("OUTAGE"));
        assertEquals("MEDIUM", ticketService.assignPriority("TECHNICAL_SUPPORT"));
        assertEquals("LOW", ticketService.assignPriority("BILLING_AND_ACCOUNTS"));
        assertEquals("LOW", ticketService.assignPriority("OTHER"));
        assertEquals("LOW", ticketService.assignPriority("UNKNOWN_TYPE"));
    }
    
    @Test
     void testGetTicketCountByLocation() {
        // Mock the repository to return a list of results
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{"Location1", 10});
        results.add(new Object[]{"Location2", 5});
        when(ticketRepository.getTicketCountByLocation()).thenReturn(results);

        // Call the service method
        List<Map<String, Object>> result = ticketService.getTicketCountByLocation();

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Location1", result.get(0).get("location"));
        assertEquals(10, result.get(0).get(TICKET_MESSAGE));
        assertEquals("Location2", result.get(1).get("location"));
        assertEquals(5, result.get(1).get(TICKET_MESSAGE));
        verify(ticketRepository, times(1)).getTicketCountByLocation();
    }
    @Test
    void testGetTicketCountByCity() {
        // Mock the repository to return a list of results
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{"City1", 10});
        results.add(new Object[]{"City2", 5});
        when(ticketRepository.getTicketCountByCity()).thenReturn(results);

        // Call the service method
        List<Map<String, Object>> result = ticketService.getTicketCountByCity();

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("City1", result.get(0).get("city"));
        assertEquals(10, result.get(0).get(TICKET_MESSAGE));
        assertEquals("City2", result.get(1).get("city"));
        assertEquals(5, result.get(1).get(TICKET_MESSAGE));
        verify(ticketRepository, times(1)).getTicketCountByCity();
    }
    
    @Test
     void testGetTicketCountByDept() {
        // Mock the repository to return a list of results
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{"Dept1", 10});
        results.add(new Object[]{"Dept2", 5});
        when(ticketRepository.countTicketsByEmployeeDept()).thenReturn(results);

        // Call the service method
        List<Map<String, Object>> result = ticketService.getTicketCountByDept();

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dept1", result.get(0).get("employeeDept"));
        assertEquals(10, result.get(0).get(TICKET_MESSAGE));
        assertEquals("Dept2", result.get(1).get("employeeDept"));
        assertEquals(5, result.get(1).get(TICKET_MESSAGE));
        verify(ticketRepository, times(1)).countTicketsByEmployeeDept();
    }
    
    @Test
     void testGetOpenTicketCount() {
        // Mock the repository to return the count
        when(ticketRepository.countTicketsByOpenStatus()).thenReturn(10L);

        // Call the service method
        long result = ticketService.getOpenTicketCount();

        // Verify the result
        assertEquals(10, result);
        verify(ticketRepository, times(1)).countTicketsByOpenStatus();
    }
    
    @Test
     void testGetClosedTicketCount() {
        // Mock the repository to return the count
        when(ticketRepository.countTicketsByClosedStatus()).thenReturn(5L);

        // Call the service method
        long result = ticketService.getClosedTicketCount();

        // Verify the result
        assertEquals(5, result);
        verify(ticketRepository, times(1)).countTicketsByClosedStatus();
    }
    
    @Test
     void testGetPendingTicketCount() {
        // Mock the repository to return the count
        when(ticketRepository.countTicketsByPendingStatus()).thenReturn(3L);

        // Call the service method
        long result = ticketService.getPendingTicketCount();

        // Verify the result
        assertEquals(3, result);
        verify(ticketRepository, times(1)).countTicketsByPendingStatus();
    }
    
    @Test
     void testGetAvgResponseTimeByManager() {
        // Mock the repository to return a list of results
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{1, 1, 2.5});
        results.add(new Object[]{2, 1, 3.0});
        when(ticketRepository.findAvgResponseTimeByManager(1)).thenReturn(results);

        // Call the service method
        List<Map<String, Object>> result = ticketService.getAvgResponseTimeByManager(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("employeeId"));
        assertEquals(1, result.get(0).get("managerId"));
        assertEquals(2.5, result.get(0).get("avgResponseTime"));
        assertEquals(2, result.get(1).get("employeeId"));
        assertEquals(1, result.get(1).get("managerId"));
        assertEquals(3.0, result.get(1).get("avgResponseTime"));
        verify(ticketRepository, times(1)).findAvgResponseTimeByManager(1);
    }
    
    @Test
    void testGetAvgResolutionTimesByManager() {
        // Mock the repository to return a list of employees
        List<Employee> employees = new ArrayList<>();
        Employee employee1 = new Employee();
        employee1.setEmployeeId(1);
        Employee employee2 = new Employee();
        employee2.setEmployeeId(2);
        employees.add(employee1);
        employees.add(employee2);
        when(employeeRepository.findByEmployeeManagerId(1)).thenReturn(employees);

        // Mock the repository to return a list of results
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{1, 2.5});
        results.add(new Object[]{2, 3.0});
        when(ticketRepository.getAvgResolutionTimesForEmployees(anyList())).thenReturn(results);

        // Call the service method
        List<Map<String, Object>> result = ticketService.getAvgResolutionTimesByManager(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("employeeId"));
        assertEquals(2.5, result.get(0).get("avgResolutionTime"));
        assertEquals(1, result.get(0).get("managerId"));
        assertEquals(2, result.get(1).get("employeeId"));
        assertEquals(3.0, result.get(1).get("avgResolutionTime"));
        assertEquals(1, result.get(1).get("managerId"));
        verify(ticketRepository, times(1)).getAvgResolutionTimesForEmployees(anyList());
    }
    
    @Test
     void testGetTicketCountForManager() {
        // Mock the repository to return the count
        when(ticketRepository.countTicketsByManagerId(1)).thenReturn(15L);

        // Call the service method
        long result = ticketService.getTicketCountForManager(1);

        // Verify the result
        assertEquals(15, result);
        verify(ticketRepository, times(1)).countTicketsByManagerId(1);
    }
    
    @Test
     void testCalculateTurnAroundTime() {
        // Create two LocalDateTime instances
        LocalDateTime raiseDate = LocalDateTime.of(2023, 10, 1, 12, 0, 0);
        LocalDateTime resolveDate = LocalDateTime.of(2023, 10, 2, 13, 30, 45);

        // Calculate the expected duration
        String expectedDuration = "1 days 1 hours 30 min 45 sec";

        // Call the method and verify the result
        String result = ticketService.calculateTurnAroundTime(raiseDate, resolveDate);
        assertEquals(expectedDuration, result);
    }

    @Test
     void testCalculateTurnAroundTime_SameDay() {
        // Create two LocalDateTime instances on the same day
        LocalDateTime raiseDate = LocalDateTime.of(2023, 10, 1, 12, 0, 0);
        LocalDateTime resolveDate = LocalDateTime.of(2023, 10, 1, 13, 30, 45);

        // Calculate the expected duration
        String expectedDuration = "0 days 1 hours 30 min 45 sec";

        // Call the method and verify the result
        String result = ticketService.calculateTurnAroundTime(raiseDate, resolveDate);
        assertEquals(expectedDuration, result);
    }

    @Test
    void testCalculateTurnAroundTime_ExactHour() {
        // Create two LocalDateTime instances with an exact hour difference
        LocalDateTime raiseDate = LocalDateTime.of(2023, 10, 1, 12, 0, 0);
        LocalDateTime resolveDate = LocalDateTime.of(2023, 10, 1, 13, 0, 0);

        // Calculate the expected duration
        String expectedDuration = "0 days 1 hours 0 min 0 sec";

        // Call the method and verify the result
        String result = ticketService.calculateTurnAroundTime(raiseDate, resolveDate);
        assertEquals(expectedDuration, result);
    }

    @Test
    void testCalculateTurnAroundTime_ExactMinute() {
        // Create two LocalDateTime instances with an exact minute difference
        LocalDateTime raiseDate = LocalDateTime.of(2023, 10, 1, 12, 0, 0);
        LocalDateTime resolveDate = LocalDateTime.of(2023, 10, 1, 12, 1, 0);

        // Calculate the expected duration
        String expectedDuration = "0 days 0 hours 1 min 0 sec";

        // Call the method and verify the result
        String result = ticketService.calculateTurnAroundTime(raiseDate, resolveDate);
        assertEquals(expectedDuration, result);
    }

    @Test
    void testCalculateTurnAroundTime_ExactSecond() {
        // Create two LocalDateTime instances with an exact second difference
        LocalDateTime raiseDate = LocalDateTime.of(2023, 10, 1, 12, 0, 0);
        LocalDateTime resolveDate = LocalDateTime.of(2023, 10, 1, 12, 0, 1);

        // Calculate the expected duration
        String expectedDuration = "0 days 0 hours 0 min 1 sec";

        // Call the method and verify the result
        String result = ticketService.calculateTurnAroundTime(raiseDate, resolveDate);
        assertEquals(expectedDuration, result);
    }
    
    @Test
     void testReassignTicketToEmployee_WithEligibleEmployees() {
        // Create a Ticket object
        Ticket ticket = new Ticket();
        ticket.setTicketType("TECHNICAL_SUPPORT");

        // Create a list of eligible employees
        List<Employee> employees = new ArrayList<>();
        Employee employee1 = new Employee();
        employee1.setEmployeeId(1);
        employee1.setEmployeeDept("TECHNICAL_SUPPORT");
        Employee employee2 = new Employee();
        employee2.setEmployeeId(2);
        employee2.setEmployeeDept("TECHNICAL_SUPPORT");
        employees.add(employee1);
        employees.add(employee2);

        // Mock the employee repository to return the list of employees
        when(employeeRepository.findByEmployeeDesignation("Employee")).thenReturn(employees);

        // Mock the ticket repository to return ticket counts
        when(ticketRepository.countByEmployeeId(1)).thenReturn(5);
        when(ticketRepository.countByEmployeeId(2)).thenReturn(3);

        // Call the method
        ticketService.reassignTicketToEmployee(ticket);

        // Verify the result
        assertEquals(2, ticket.getEmployeeId());
    }
    @Test
    void testReassignTicketToEmployee_WithoutEligibleEmployees() {
        // Create a Ticket object
        Ticket ticket = new Ticket();
        ticket.setTicketType("TECHNICAL_SUPPORT");

        // Mock the employee repository to return an empty list
        when(employeeRepository.findByEmployeeDesignation("Employee")).thenReturn(new ArrayList<>());

        // Call the method
        ticketService.reassignTicketToEmployee(ticket);

        // Verify the result
        assertEquals(-1, ticket.getEmployeeId()); // Assuming -1 means no employee assigned
    }
  
    @Test
    void testProcessCustomerUpdates() {
        // Arrange
        int newRating =5;
        String newFeedback = "Excellent service!";
        
        // Stubbing the updatedTicket to return values
        when(updtticket.getCustomerRating()).thenReturn(newRating);
        when(updtticket.getCustomerFeedback()).thenReturn(newFeedback);

        // Act
        // Call the method that we want to test
        ticketService.processCustomerUpdates(existticket, updtticket);

        // Assert
        // Verify that the existingTicket's methods were called with the new values
        verify(existticket).setCustomerRating(newRating);
        verify(existticket).setCustomerFeedback(newFeedback);
       
    }
    



    @Test
     void testUpdateTicketDetails_ClosedTicketEmail() {
        int ticketId = 1;

        // Mocking the existingTicket object
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existticket));
        when(existticket.getCustomer()).thenReturn(cus);
        when(cus.getCustomerEmail()).thenReturn("customer@example.com");

        // Stubbing for status change to CLOSED
        when(existticket.getTicketStatus()).thenReturn("OPEN");
        when(existticket.getTicketType()).thenReturn("TECHNICAL_SUPPORT");
        when(existticket.getTicketPriority()).thenReturn("HIGH");

        when(updtticket.getTicketStatus()).thenReturn("CLOSED");
        when(updtticket.getTicketType()).thenReturn("TECHNICAL_SUPPORT");
        when(updtticket.getTicketPriority()).thenReturn("HIGH");

        // Assume feedback stays the same (or doesn't affect status change)
        when(updtticket.getCustomerFeedback()).thenReturn("Great service");
        
        // Stubbing other methods called inside updateTicketDetails
        when(ticketRepository.save(existticket)).thenReturn(existticket);

        // Act
        Ticket savedTicket = ticketService.updateTicketDetails(ticketId, updtticket);

        // Assert: Verify that the closed ticket email is sent
        verify(emailService).sendClosedTicketEmail(
                ("customer@example.com"), 
                (savedTicket.getTicketId()),
                (updtticket.getEmployeeComment())
        );
    }
        
        @Test
         void testGetAverageResponseTimeForEmployee() {
            // Mock the repository response
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{1, 15.5});  // Employee 1 with avg response time of 15.5
            mockResults.add(new Object[]{1, 10.0});  // Employee 1 with avg response time of 10.0

            // Mock the behavior of the repository
            when(ticketRepository.getAvgResponseTimeForEmployee(1)).thenReturn(mockResults);

            // Call the method
            List<Map<String, Object>> result = ticketService.getAverageResponseTimeForEmployee(1);

            // Verify the behavior
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1, result.get(0).get("employeeId"));
            assertEquals(15.5, result.get(0).get("avgResponseTime"));
        
    }
        
        @Test
         void testGetAvgResolutionTimeByEmployeeForMonth() {
            // Mock the repository response
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{1, 5, 2023, new BigDecimal("20.5")});  // Employee 1, May 2023, avgResolutionTime = 20.5
            mockResults.add(new Object[]{1, 6, 2023, new BigDecimal("25.0")});  // Employee 1, June 2023, avgResolutionTime = 25.0

            // Mock the behavior of the repository
            when(ticketRepository.findAvgResolutionTimeByEmployeeForEachMonth(1)).thenReturn(mockResults);

            // Call the method
            List<Map<String, Object>> result = ticketService.getAvgResolutionTimeByEmployeeForMonth(1);

            // Verify the behavior
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(5, result.get(0).get("month"));
            assertEquals(2023, result.get(0).get("year"));
            assertEquals(20.5, result.get(0).get("avgResolutionTime"));
            assertEquals(6, result.get(1).get("month"));
            assertEquals(2023, result.get(1).get("year"));
            assertEquals(25.0, result.get(1).get("avgResolutionTime"));
        }
    @Test
    void testDeleteTicketByTicketId() {
        // Call the service method
        ticketService.deleteTicketByTicketId(1);

        // Verify the repository was called
        verify(ticketRepository, times(1)).deleteById(1);
    }
   
}