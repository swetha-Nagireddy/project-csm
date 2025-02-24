package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.DuplicateTicketException;
import com.example.demo.exceptions.InvalidTicketStatusException;
import com.example.demo.exceptions.TicketNotFoundException;
import com.example.demo.model.Ticket;
import com.example.demo.service.TicketService;


import jakarta.validation.Valid;

@RestController
@RequestMapping(value="/ticket")
@CrossOrigin(origins = "http://localhost:3000")
public class TicketController {
   
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
    	this.ticketService = ticketService;
    }
    
    @PostMapping(value = "/addTicket")
    public ResponseEntity<Object> createTicket(@Valid @RequestBody Ticket ticket) {
        try {
            Ticket createdTicket = ticketService.addTicket(ticket);
            return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
        } catch (DuplicateTicketException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    public class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    @GetMapping(value = "/showTicket")
    public List<Ticket> showTicket() {
        return ticketService.showTicket();
    }
    
    @GetMapping(value="/searchTicketByTicketId/{id}")
    public ResponseEntity<Ticket> get(@PathVariable int id) {
    	 try {
             Ticket ticket = ticketService.searchTicketByTicketId(id);
             return new ResponseEntity<>(ticket, HttpStatus.OK);
             
         } catch (TicketNotFoundException e) {
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         }
    }
 
    @GetMapping(value="/searchTicketByCustomerId/{customerId}")
    public ResponseEntity<List<Ticket>> searchTicketByCustomerId(@PathVariable int customerId) {
        List<Ticket> ticketList = ticketService.searchTicketByCustomerId(customerId);
        if(ticketList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticketList, HttpStatus.OK);
    }
    
    @GetMapping(value="/searchTicketByEmployeeId/{employeeId}")
    public ResponseEntity<List<Ticket>> searchTicketByEmpId(@PathVariable int employeeId) {
        List<Ticket> ticketList = ticketService.searchTicketByEmployeeId(employeeId);
        if(ticketList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticketList, HttpStatus.OK);
    }
    
    @PutMapping("updateTicket/{ticketId}")
    public ResponseEntity<Ticket> updateTicketDetails(
            @PathVariable int ticketId,
            @RequestBody Ticket updatedTicket) {
        Ticket ticket = ticketService.updateTicketDetails(ticketId, updatedTicket);
        return ResponseEntity.ok(ticket);
    }
    
    @PutMapping("/closeTicketByCustomer/{ticketId}")
    public ResponseEntity<Ticket> closeTicketByCustomer(@PathVariable int ticketId) {
        try {
            Ticket ticket = ticketService.closeTicketByCustomer(ticketId);
            return ResponseEntity.ok(ticket);
        } catch (TicketNotFoundException ex) {
            // Return 404 NOT_FOUND status if the ticket is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Or return a custom error message as needed
        } catch (InvalidTicketStatusException ex) {
            // Return 400 BAD_REQUEST if the status of the ticket is invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Or return a custom error message as needed
        } catch (Exception ex) {
            // Return 500 INTERNAL_SERVER_ERROR for any unforeseen errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    

    @PostMapping("/reopenTicket/{ticketId}")
    public ResponseEntity<Ticket> reopenTicket(@PathVariable int ticketId) {
        try {
            Ticket ticket = ticketService.reopenTicket(ticketId);
            return ResponseEntity.ok(ticket);
        } catch (TicketNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (InvalidTicketStatusException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); 
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
 
    @GetMapping("/city-ticket-count")
    public ResponseEntity<List<Map<String, Object>>> getTicketCountByCity() {
        List<Map<String, Object>> ticketCounts = ticketService.getTicketCountByCity();
        return ResponseEntity.ok(ticketCounts);
    }
    
    @GetMapping("/countByLocation")
    public ResponseEntity<List<Map<String, Object>>> getTicketCountByLocation() {
        return ResponseEntity.ok(ticketService.getTicketCountByLocation());
    }
    
    @GetMapping("/countByDept")
    public List<Map<String, Object>> getTicketCountByDept() {
        return ticketService.getTicketCountByDept();
    }    
    
    @GetMapping("/open/count")
    public long getOpenTicketCount() {
        return ticketService.getOpenTicketCount();
    }
 
    @GetMapping("/closed/count")
    public long getClosedTicketCount() {
        return ticketService.getClosedTicketCount();
    }
 
    @GetMapping("/pending/count")
    public long getPendingTicketCount() {
        return ticketService.getPendingTicketCount();
    }
  
    @GetMapping("/avgresponsetime/{managerId}")
    public List<Map<String, Object>> getAvgResponseTimeByManager(@PathVariable int managerId) {
        return ticketService.getAvgResponseTimeByManager(managerId);
    }
    
    @GetMapping("/avgResolutionTimesByManager/{managerId}")
    public List<Map<String, Object>> getAvgResolutionTimesByManager(@PathVariable Integer managerId) {
        return ticketService.getAvgResolutionTimesByManager(managerId);
    }
    
    
    @GetMapping("/ticketscountemployeeundermanager/{managerId}")
    public long getTicketCount(@PathVariable int managerId) {
        return ticketService.getTicketCountForManager(managerId);
    }
    
    @GetMapping("/average-response-time/{employeeId}")
    public ResponseEntity<List<Map<String, Object>>> getAverageResponseTimeForEmployee(@PathVariable int employeeId) {
        List<Map<String, Object>> avgResponseTimeList = ticketService.getAverageResponseTimeForEmployee(employeeId);
       
        if (!avgResponseTimeList.isEmpty()) {
            return ResponseEntity.ok(avgResponseTimeList);
        } else {
            return ResponseEntity.notFound().build();  // 404 if no data
        }
    }
   
    @GetMapping("/avg-resolution/{employeeId}")
    public List<Map<String, Object>> getAvgResolutionData(@PathVariable int employeeId) {
        return ticketService.getAvgResolutionTimeByEmployeeForMonth(employeeId);
    }
    
    @DeleteMapping(value="/deleteTicketById/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable int ticketId) {
        try {
            ticketService.deleteTicketByTicketId(ticketId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
