// // package com.doconnect.userservice;

// // import org.springframework.boot.test.context.SpringBootTest;

// // @SpringBootTest
// // class UserServiceApplicationTests {

// // 	@Test
// // 	void contextLoads() {
// // 	}

// // }


// package com.doconnect.userservice.security;


// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration
// public class SecurityConfig {

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }

//       <!-- SECURITY -->
//         <dependency>
//             <groupId>org.springframework.boot</groupId>
//             <artifactId>spring-boot-starter-security</artifactId>
//         </dependency>
