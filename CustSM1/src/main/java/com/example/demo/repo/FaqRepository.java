package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Faq;
/**
 * FaqRespository Interface
 * This interface is a repository for Chatbot related functionalities
 * 
 * @author Manoj.KS
 */
public interface FaqRepository extends JpaRepository<Faq, Long> {
	
	Optional<Faq> findById(Long faqId);

}
