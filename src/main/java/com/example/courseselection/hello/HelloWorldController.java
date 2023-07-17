package com.example.courseselection.hello;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseselection.annotation.TrackTime;
import com.example.courseselection.config.CustomeCorsConfiguration;
import com.example.courseselection.service.MailService;

@RestController
public class HelloWorldController {
	
	@Value("${spring.profiles.active:}")
	private String activeProfile;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private CustomeCorsConfiguration corsConfiguration;
	
	@GetMapping("/hello")
	public String hello() {
		Instant currentTime = Instant.now();
		return "Welcome to course selection. Current time is ".concat(currentTime.toString());
	}
	
	@GetMapping("/config")
	public List<String> configurationProperties() {
		return Arrays.asList(
				mailService.getBase(),
				mailService.getFrom());
	}
	
	@GetMapping("/cors-config")
	public CustomeCorsConfiguration corsConfigurationProperties() {
		return corsConfiguration;
	}
	
	@GetMapping("/profile")
	public String activeProfile() {
		return activeProfile;
	}
	
	@GetMapping("/track")
	@TrackTime
	public String trackTime() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "This is testing tracking time.";
	}

}
