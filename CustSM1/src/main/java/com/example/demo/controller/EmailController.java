package com.example.demo.controller;
 
import com.example.demo.service.EmailService;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/email")
public class EmailController {
 
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
    	this.emailService = emailService;
    }
 
    @PostMapping("/send")
    public String sendEmail(@RequestParam String toEmail,
                            @RequestParam String subject,
                            @RequestParam String body) {
        return emailService.sendEmail(toEmail, subject, body);
    }
}