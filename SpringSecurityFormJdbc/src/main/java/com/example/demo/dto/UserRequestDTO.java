package com.example.demo.dto;


import lombok.Data;

//when request will be send consist of json data it will interact with DTO first 
@Data
public class UserRequestDTO {
	

	private String username;
	private String password;
	private String role;

}