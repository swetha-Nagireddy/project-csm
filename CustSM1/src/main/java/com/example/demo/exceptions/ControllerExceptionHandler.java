package com.example.demo.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;




@RestControllerAdvice
public class ControllerExceptionHandler {

	 @ExceptionHandler(ResourceNotFoundException.class)
	 @ResponseStatus(value = HttpStatus.NOT_FOUND)
	  public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
	    return new ErrorMessage(
	        HttpStatus.NOT_FOUND.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	  }
	  
	  @ExceptionHandler(Exception.class)
	  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	  public ErrorMessage globalExceptionHandler(Exception ex, WebRequest request) {
	    return new ErrorMessage(
	        HttpStatus.INTERNAL_SERVER_ERROR.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	  }
	  
	  @ExceptionHandler(InvalidTicketStatusException.class)
	    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	    public ErrorMessage handleInvalidTicketStatusException(InvalidTicketStatusException ex, WebRequest request) {
	        return new ErrorMessage(
	            HttpStatus.BAD_REQUEST.value(),
	            new Date(),
	            ex.getMessage(),
	            request.getDescription(false)
	        );
	    }

	  

	    @ExceptionHandler(TicketNotFoundException.class)
	    @ResponseStatus(value = HttpStatus.NOT_FOUND)
	    public ErrorMessage ticketNotFoundException(TicketNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(), request.getDescription(false));
	    }
	    
	    @ExceptionHandler(CustomerNotFoundException.class)
	    @ResponseStatus(value = HttpStatus.NOT_FOUND)
	    public ErrorMessage handleCustomerNotFoundException(CustomerNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(), request.getDescription(false));
	    }

	    @ExceptionHandler(InvalidCredentialsException.class)
	    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	    public ErrorMessage handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
	        return new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), new Date(), ex.getMessage(), request.getDescription(false));
	    }

	    @ExceptionHandler(EmailNotFoundException.class)
	    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	    public ErrorMessage handleEmailNotFoundException(EmailNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), new Date(), ex.getMessage(), request.getDescription(false));
	    }
	    
	    @ExceptionHandler(EmployeeNotFoundException.class)
	    @ResponseStatus(value = HttpStatus.NOT_FOUND)
	    public ErrorMessage handleEmployeeNotFoundException(EmployeeNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(
	            HttpStatus.NOT_FOUND.value(),
	            new Date(),
	            ex.getMessage(),
	            request.getDescription(false)
	        );
	    }

	    @ExceptionHandler(InvalidOtpException.class)
	    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	    public ErrorMessage handleInvalidOtpException(InvalidOtpException ex, WebRequest request) {
	        return new ErrorMessage(
	            HttpStatus.BAD_REQUEST.value(),
	            new Date(),
	            ex.getMessage(),
	            request.getDescription(false)
	        );
	    }

	    @ExceptionHandler(InvalidRoleException.class)
	    @ResponseStatus(value = HttpStatus.FORBIDDEN)
	    public ErrorMessage handleInvalidRoleException(InvalidRoleException ex, WebRequest request) {
	        return new ErrorMessage(
	            HttpStatus.FORBIDDEN.value(),
	            new Date(),
	            ex.getMessage(),
	            request.getDescription(false)
	        );
	    }
	    
	    @ExceptionHandler(CustomerAlreadyExistsException.class)
	    @ResponseStatus(value = HttpStatus.CONFLICT)  // 409 Conflict status
	    public ErrorMessage handleCustomerAlreadyExistsException(CustomerAlreadyExistsException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.CONFLICT.value(),  // Status code
	                new Date(),  // Timestamp
	                ex.getMessage(),  // Error message
	                request.getDescription(false)  // Request details
	        );
	    }
	    
	    @ExceptionHandler(InvalidTicketOperationException.class)
	    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	    public ErrorMessage handleInvalidTicketOperationException(InvalidTicketOperationException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.BAD_REQUEST.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	    }


	    @ExceptionHandler(TicketAssignmentException.class)
	    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	    public ErrorMessage ticketAssignmentException(TicketAssignmentException ex, WebRequest request) {
	        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), new Date(), ex.getMessage(), request.getDescription(false));
	    }
	    
	    @ExceptionHandler(DuplicateTicketException.class)
	    @ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400 Bad Request
	    public ErrorMessage handleDuplicateTicketException(DuplicateTicketException ex, WebRequest request) {
	        // Create the custom error message
	        return new ErrorMessage(
	            HttpStatus.BAD_REQUEST.value(),
	            new Date(),
	            ex.getMessage(), // Exception message
	            request.getDescription(false)); // Return the error message as the response body
	    }
	    
	    @ExceptionHandler(OtpEmailSendingException.class)
	    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)  // HTTP status for this error
	    public ErrorMessage handleOtpEmailSendingFailure(OtpEmailSendingException ex, WebRequest request) {
	        // Return a custom error message
	        return new ErrorMessage(
	                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // Status code
	                new Date(),  // Current timestamp
	                ex.getMessage(),  // Custom message from exception
	                request.getDescription(false)  // Details from the request (optional)
	        );
	    }
 

}