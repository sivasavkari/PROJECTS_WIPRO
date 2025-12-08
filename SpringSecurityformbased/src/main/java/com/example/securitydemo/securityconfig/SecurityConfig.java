package com.example.securitydemo.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// which of the below urls are secured / open and login method
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	        .csrf(csrf ->csrf.disable()) //  to disable for simple rest demo ; enable for forms
	            .authorizeHttpRequests(authorize -> authorize
	                .requestMatchers("/public/**").permitAll() // Allow public access to /public/**
	                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Require ADMIN role for /admin/**
	                .requestMatchers("/api/user/**").hasAnyRole("USER","ADMIN") // admin and user can access
	                .anyRequest().authenticated() // All other requests require authentication
	            )
	            .httpBasic(Customizer.withDefaults())
	            .formLogin(Customizer.withDefaults()); // by default login and logout urls are permitted for all 
	           
	        return http.build();
	    }
        
	  // In- memory users for testing
	    @Bean
	    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
	        UserDetails user = User.builder()
	            .username("jiya")
	            .password(passwordEncoder.encode("password"))
	            .roles("USER")
	            .build();

	        UserDetails admin = User.builder()
	            .username("admin")
	            .password(passwordEncoder.encode("adminpass"))
	            .roles("ADMIN", "USER")
	            .build();

	        return new InMemoryUserDetailsManager(user, admin);
	    }
	    
	    
	    @Bean
	    public PasswordEncoder passwordEncoder()
	    {
	    	return new BCryptPasswordEncoder();
	    }
	    
	    
	
}