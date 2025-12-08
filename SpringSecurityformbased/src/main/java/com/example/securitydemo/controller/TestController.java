package com.example.securitydemo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TestController {
	
	
	@GetMapping("/public/welcome")
	public String getWelcome()
	{
			
		return "Welome to our Hotel";
	}
	
	
	@GetMapping("/api/user/userinfo")
	public String getVoucher()
	{
			
		return "Your voucher code is generated:";
	}
	
	@GetMapping("/api/admin/admininfo")
	public String getUserInfo()
	{
			
		return "admin dashboard will get open";
	}
	
	
	

}
