package com.example.demo.service;
 
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.OtpEmailSendingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
 
@Service
public class EmailService {
	
    private final JavaMailSender javaMailSender; // Autowired JavaMailSender

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
   
    private String str = "' to '";
    
    private static final Logger logger = Logger.getLogger(EmailService.class);
    
    /**
     * Sends a plain text email to the specified recipient with the provided subject and body.
     * 
     */
    
    public String sendEmail(String toEmail, String subject, String body) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
 
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, false); // Set false to send plain text, change to true for HTML
            
            logger.debug("MIME message created, attempting to send");
            
            javaMailSender.send(mimeMessage);
            return "Email sent successfully to " + toEmail;
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send email: " + e.getMessage();
        }
    }
    
    /**
     * Sends an OTP email to the specified email address.
     * The email contains the OTP for password reset.
     */
    
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your OTP for Password Reset");
            helper.setText("Your OTP is: " + otp, true);
            javaMailSender.send(message);
            logger.info("OTP email sent successfully to: "+ to);
        } catch (MessagingException e) {
            throw new OtpEmailSendingException("Email sending error");
        }
    }
    
    /**
     * Sends an email notifying the customer about the update in their ticket status.
     * It includes details like status change, ticket type change, and ticket priority change.
     */
    
    public String sendTicketUpdateEmail(String toEmail, int ticketId, String oldStatus, String newStatus,
            String oldTicketType, String newTicketType, String newTicketPriority) {
        String subject = "Update on Your Ticket #" + ticketId;
        StringBuilder body = new StringBuilder();
        body.append("Dear Customer,\n\n");
        body.append("Your ticket #" + ticketId + " has been updated:\n");
        
        if (!oldStatus.equals(newStatus)) {
            body.append(" - Status changed from '").append(oldStatus).append(str).append(newStatus).append("'\n");
        }
        
        if (oldTicketType != null && !oldTicketType.equals(newTicketType)) {
            body.append(" - Ticket Type changed from '").append(oldTicketType).append(str).append(newTicketType).append("'\n");
        }
        body.append(" - Ticket Priority changed").append(str).append(newTicketPriority).append("'\n");
        
        body.append("\nThank you for your patience.\n");
        logger.debug("Email body constructed, attempting to send");
        
        return sendEmail(toEmail, subject, body.toString());
    }
    
    /**
     * Sends an email to notify the customer that their ticket has been successfully closed.
     * Includes employee comments (if any) about the closure of the ticket.
     */
    
    public String sendClosedTicketEmail(String customerEmail, int ticketId, String employeeComments) {
        String subject = "Ticket #" + ticketId + " Successfully Closed";
        StringBuilder body = new StringBuilder();
        
        body.append("Dear Customer,\n\n")
            .append("Your ticket has been successfully closed.\n\n")
            .append("Employee Comments: ")
            .append(employeeComments != null ? employeeComments : "No comments provided")
            .append("\n\n")
            .append("Please provide your feedback on the ticket.\n\n")
            .append("Thank you for using our service!\n");
        
        logger.debug("Email body constructed, attempting to send");
        return sendEmail(customerEmail, subject, body.toString());
    }
}
