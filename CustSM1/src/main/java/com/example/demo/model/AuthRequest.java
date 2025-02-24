package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AuthRequest Class
 * This class is used to represent the authentication request.
 * It contains the necessary details required for the authentication process like username, password, and designation.
 * 
 * @author Nitisha.S
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
	
    private String username;
    private String password;
    private String designation;
}
