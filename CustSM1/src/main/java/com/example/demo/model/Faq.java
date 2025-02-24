package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Faq")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Faq {
	
    @Id
    @Column(name="FAQ_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="QUESTION")
    private String question;
    
    @Column(name="ANSWER")
    private String answer;
    
}
