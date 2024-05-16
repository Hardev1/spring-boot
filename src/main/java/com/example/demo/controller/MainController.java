package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

	@GetMapping("/")
	public String goHome() {
		return "/home";
	}
	
	@GetMapping("/hello")
	public String goHello() {
		return "/hola";
	}
	
	@GetMapping("/login")
	public String goLogin() {
		return "/login";
	}

}