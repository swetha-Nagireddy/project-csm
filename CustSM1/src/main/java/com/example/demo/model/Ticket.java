package com.example.demo.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ticket Entity Class
 * Represents a support ticket raised by an employee in the system. This class maps to the "TICKET" table in the database.
 * It contains attributes for ticket details, status, priority, timestamps, and feedback provided by the customer.
 * @authors Srihari.P, Satwik.A
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="TICKET")
public class Ticket {
    
    /**
     * Unique identifier for each ticket.
     * This field is automatically generated as a primary key for the ticket.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TICKET_ID")
    private int ticketId;
    
    /**
     * Employee ID that raised the ticket.
     */
    @Column(name="EMPLOYEE_ID")
    private int employeeId;
    
    /**
     * Type of the ticket.
     */
    @Column(name="TICKET_TYPE")
    private String ticketType;
    
    /**
     * Description of the issue raised in the ticket.
     */
    @Column(name="TICKET_DESCRIPTION")
    private String ticketDescription;
    
    /**
     * Date and time when the ticket was raised.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name="TICKET_RAISEDATE", nullable=false)
    private LocalDateTime ticketRaiseDate;
    
    /**
     * Current status of the ticket.
     * Stores the status of the ticket (e.g., Open, Closed, In Progress).
     */
    @Column(name="TICKET_STATUS")
    private String ticketStatus;
    
    /**
     * Priority level of the ticket.
     * Stores the priority assigned to the ticket (e.g., High, Medium, Low).
     */
    @Column(name="TICKET_PRIORITY")
    private String ticketPriority;
    
    /**
     * Date and time when a response was made to the ticket.
     */
    @Column(name="RESPONSE_TIME")
    private LocalDateTime responseTime;
    
    /**
     * Date and time when the ticket was resolved.
     */
    @Column(name="RESOLVE_TIME")
    private LocalDateTime resolveTime;
    
    /**
     * Employee's comment on the ticket.
     */
    @Column(name="EMPLOYEE_COMMENT")
    private String employeeComment;
    
    /**
     * Turnaround time for resolving the ticket.
     */
    @Column(name = "TURN_AROUND_TIME")
    private String turnAroundTime;
    
    /**
     * Rating provided by the customer for the resolution of the ticket.
     */
    @Column(name="CUSTOMER_RATING")
    private Integer customerRating;
    
    /**
     * Feedback provided by the customer after ticket resolution.
     */
    @Column(name="CUSTOMER_FEEDBACK")
    private String customerFeedback;
    
    /**
     * Relationship with the Customer entity.
     */
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "CUSTOMER_ID", nullable=false)
    private Customer customer;
}
