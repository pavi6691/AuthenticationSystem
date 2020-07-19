package com.praveen.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
	
	@RequestMapping("/Welcome")
	public String welcome() {
		return "Welcome!, You are in!";
	}
}
