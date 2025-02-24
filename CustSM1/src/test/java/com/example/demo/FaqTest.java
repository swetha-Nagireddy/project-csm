package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.demo.model.Faq;

class FaqTest {

	private Faq faq;

	@BeforeEach
	void setUp() {
		faq = new Faq();
	}

	@Test
	void testNoArgsConstructor() {

		Faq newFaq = new Faq();
		assertNotNull(newFaq);
	}

	@Test
	void testAllArgsConstructor() {

		Faq newFaq = new Faq(1L, "How can I check my internet speed?",
				"You can check your internet speed using online tools like Speedtest.net or through our mobile app under the \"Speed Test\" section.");
		assertEquals(1L, newFaq.getId());
		assertEquals("How can I check my internet speed?", newFaq.getQuestion());
		assertEquals("You can check your internet speed using online tools like Speedtest.net or through our mobile app under the \"Speed Test\" section.",
				newFaq.getAnswer());
	}

	@Test
	void testSettersAndGetters() {
		faq.setId(2L);
		faq.setQuestion("How do I reset my router?");
		faq.setAnswer("Unplug your router from the power source, wait for 30 seconds, and plug it back in. Wait for the lights to stabilize before reconnecting.");
		assertEquals(2L, faq.getId());
		assertEquals("How do I reset my router?", faq.getQuestion());
		assertEquals("Unplug your router from the power source, wait for 30 seconds, and plug it back in. Wait for the lights to stabilize before reconnecting.",
				faq.getAnswer());
	}

	@Test
	void testIdNullable() {

		faq.setId(null);
		assertNull(faq.getId());
	}

}
