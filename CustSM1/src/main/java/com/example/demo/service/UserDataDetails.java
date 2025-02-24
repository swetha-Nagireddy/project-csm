package com.example.demo.service;
 
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
 
import com.example.demo.model.Employee;
 
 
public class UserDataDetails implements UserDetails {
 
	 	private String name;
	    private String password;
	    private List<GrantedAuthority> authorities;
 
	    public UserDataDetails(Employee userInfo) {
	        name = userInfo.getEmployeeEmail();
	        password = userInfo.getEmployeePassword();
	        authorities = Arrays.stream(userInfo.getEmployeeDesignation().split(","))
	                .map(SimpleGrantedAuthority::new)
	                .collect(Collectors.toList());
	    }
 
	    
	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return authorities;
	    }
 
	    @Override
	    public String getPassword() {
	        return password;
	    }
 
	    @Override
	    public String getUsername() {
	        return name;
	    }
 
	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }
 
	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }
 
	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }
 
	    @Override
	    public boolean isEnabled() {
	        return true;
	    }
}