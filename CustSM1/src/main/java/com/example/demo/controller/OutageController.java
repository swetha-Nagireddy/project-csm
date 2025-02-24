package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OutageService;

@RestController
@RequestMapping(value="/outage")
@CrossOrigin(origins = "http://localhost:3000")
public class OutageController {
    
    private final OutageService outageService;
    
    public OutageController(OutageService outageService) {
    	this.outageService = outageService;
    }
    

    @GetMapping("/outages")
    public ResponseEntity<List<Object[]>> getOutageLocations() {
    	 try {
             // Call the service method to get outage locations
             List<Object[]> outages = outageService.getOutageLocations();
 
             // Return 200 OK response if outages are found
             return ResponseEntity.ok(outages);
 
         } catch (Exception e) {
             // Exception is caught by the GlobalExceptionHandler automatically
             // No need for manual handling here since it's centralized in the handler
             return ResponseEntity.status(500).build();
         }
     }
}
