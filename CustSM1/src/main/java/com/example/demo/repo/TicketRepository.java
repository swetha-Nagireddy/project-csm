package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Ticket;

/**
 * TicketRepository Interface
 * This interface is a repository for Ticket related functionalities
 * 
 * @authors Srihari.P, Satwik.A, Manjunath.AS
 * 
 */

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
	
	// Find the outage locations and point them on map. Called in outage service
    @Query("SELECT t.customer.customerPincode AS pincode, t.customer.customerAddress AS address, " +
    	       "t.customer.customerLatitude AS latitude, t.customer.customerLongitude AS longitude, " +
    	       "COUNT(t) AS count " +
    	       "FROM Ticket t " + 
    	       "WHERE t.ticketPriority = 'HIGH' AND t.ticketType = 'OUTAGE' AND t.ticketStatus != 'CLOSED' " +  
    	       "GROUP BY t.customer.customerPincode, t.customer.customerAddress, " +
    	       "t.customer.customerLatitude, t.customer.customerLongitude " +
    	       "HAVING COUNT(t) > 2")
    	List<Object[]> findOutageLocations();
	
	    List<Ticket> findByCustomer_CustomerId(int customerId);
	    
	    List<Ticket> findByEmployeeId(int employeeId);
	    
	    List<Ticket> findByTicketStatus(String ticketStatus);

    	int countByEmployeeId(Integer employeeId);

    	int countByEmployeeIdAndTicketStatus(Integer employeeId, String status);

    	int countByEmployeeIdAndTicketPriority(Integer employeeId, String priority);

    	//state vs tickets
    	@Query("SELECT c.customerState, COUNT(t.ticketId) FROM Ticket t JOIN t.customer c GROUP BY c.customerState")
    	List<Object[]> getTicketCountByLocation();
    
	    // Query to get ticket count grouped by state and city(cust_location vs tickets )
	    @Query("SELECT  c.customerCity, COUNT(t.ticketId) " +
	           "FROM Ticket t JOIN t.customer c " +
	           "GROUP BY  c.customerCity")
	    List<Object[]> getTicketCountByCity();
    
   
	    //domain vs no of tickets
	    @Query("SELECT e.employeeDept, COUNT(t) FROM Employee e JOIN Ticket t ON e.employeeId = t.employeeId GROUP BY e.employeeDept")
	    List<Object[]> countTicketsByEmployeeDept();
	    
	    
	    // Count tickets with 'Open' status
	    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.ticketStatus = 'Open'")
	    long countTicketsByOpenStatus();
	 
	    // Count tickets with 'Closed' status
	    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.ticketStatus = 'Closed'")
	    long countTicketsByClosedStatus();
	 
	    // Count tickets with 'In Progress' status
	    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.ticketStatus = 'Pending'")
	    long countTicketsByPendingStatus();
 
	    // Custom query to get average response time by manager ID
	    @Query("SELECT t.employeeId, e.employeeManagerId, AVG(TIMESTAMPDIFF(SECOND, t.ticketRaiseDate, t.responseTime)) AS avgResponseTime " +
	           "FROM Ticket t " +
	           "JOIN Employee e ON t.employeeId = e.employeeId " +
	           "WHERE e.employeeManagerId = :managerId " +
	           "GROUP BY t.employeeId, e.employeeManagerId")
	    List<Object[]> findAvgResponseTimeByManager(@Param("managerId") int managerId);
	    
	    // Custom query to get average resolution time by manager ID
	    @Query("SELECT t.employeeId, e.employeeManagerId, AVG(TIMESTAMPDIFF(MINUTE, t.ticketRaiseDate, t.resolveTime)) AS avgResponseTime " +
	           "FROM Ticket t " +
	           "JOIN Employee e ON t.employeeId = e.employeeId " +
	           "WHERE e.employeeManagerId = :managerId " +
	           "GROUP BY t.employeeId, e.employeeManagerId")
	    List<Object[]> findAvgResolutionTimeByManager(@Param("managerId") int managerId);
    
	    //avg resolution of employees in minutes
	    @Query("SELECT t.employeeId, AVG(TIMESTAMPDIFF(MINUTE, t.ticketRaiseDate, t.resolveTime)) " +
	            "FROM Ticket t WHERE t.employeeId IN :employeeIds AND t.ticketStatus = 'closed' " +
	            "GROUP BY t.employeeId")
	    List<Object[]> getAvgResolutionTimesForEmployees(List<Integer> employeeIds);
 
	    // Custom query to calculate average response time for a specific employee
	    @Query("SELECT t.employeeId, AVG(TIMESTAMPDIFF(MINUTE, t.ticketRaiseDate, t.responseTime)) " +
	           "FROM Ticket t " +
	           "WHERE t.responseTime IS NOT NULL AND t.employeeId = :employeeId " +
	           "GROUP BY t.employeeId")
	    List<Object[]> getAvgResponseTimeForEmployee(@Param("employeeId") int employeeId);
    
	    // Average resolution time of employees by each month
	    @Query(value = "SELECT EMPLOYEE_ID, " +
	            "MONTH(TICKET_RAISEDATE) AS month, " +
	            "YEAR(TICKET_RAISEDATE) AS year, " +
	            "AVG(TIMESTAMPDIFF(SECOND, TICKET_RAISEDATE, RESOLVE_TIME)) AS avgResolutionTime " +
	            "FROM TICKET " +
	            "WHERE TICKET_STATUS = 'Closed' " +
	            "AND RESOLVE_TIME IS NOT NULL " +
	            "AND EMPLOYEE_ID = :employeeId " +  // Filter by employeeId
	            "GROUP BY EMPLOYEE_ID, YEAR(TICKET_RAISEDATE), MONTH(TICKET_RAISEDATE)", nativeQuery = true)
		List<Object[]> findAvgResolutionTimeByEmployeeForEachMonth(int employeeId);

		List<Ticket> findByCustomer_CustomerIdAndTicketTypeAndTicketStatusIn(Integer customerId, String ticketType, List<String> ticketStatus);

		// Query to get the count of tickets for employees under a specific manager
		@Query("SELECT COUNT(t) FROM Ticket t WHERE t.employeeId IN (SELECT e.employeeId FROM Employee e WHERE e.employeeManagerId = :managerId)")
		long countTicketsByManagerId(int managerId);
		
}